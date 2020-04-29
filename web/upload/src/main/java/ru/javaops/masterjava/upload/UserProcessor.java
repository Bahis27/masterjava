package ru.javaops.masterjava.upload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.dao.UserGroupDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserGroup;
import ru.javaops.masterjava.persist.model.type.UserFlag;
import ru.javaops.masterjava.upload.PayloadProcessor.FailedEmails;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

@Slf4j
public class UserProcessor {
    private static final int NUMBER_THREADS = 4;

    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
    private static UserDao userDao = DBIProvider.getDao(UserDao.class);
    private static UserGroupDao userGroupDao = DBIProvider.getDao(UserGroupDao.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    /*
     * return failed users chunks
     */
    public List<FailedEmails> process(final StaxStreamProcessor processor, Map<String, City> cities, Map<String, Group> groups, int chunkSize) throws XMLStreamException, JAXBException {
        log.info("Start processing with chunkSize=" + chunkSize);

        Map<String, Future<List<String>>> chunkFutures = new LinkedHashMap<>();  // ordered map (emailRange -> userChunk future)

        int userId = userDao.getSeqAndSkip(chunkSize);
        List<User> userChunk = new ArrayList<>(chunkSize);
        val unmarshaller = jaxbParser.createUnmarshaller();
        List<FailedEmails> failed = new ArrayList<>();

        Map<Integer, String> allNewUserEmails = new HashMap<>();
        List<UserGroup> allNewUserGroups = new ArrayList<>();
        List<String> failedEmails = new ArrayList<>();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {

            String cityRef = processor.getAttribute("city");  // unmarshal doesn't get city ref or groupRefs

            String userGroupRefs = processor.getAttribute("groupRefs");
            List<String> groupRefs = new ArrayList<>();
            if (userGroupRefs != null) {
                groupRefs = Arrays.stream(userGroupRefs.split(" "))
                        .map(String::trim)
                        .collect(Collectors.toList());
            }

            ru.javaops.masterjava.xml.schema.User xmlUser = unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);
            if (cities.get(cityRef) == null) {
                failed.add(new FailedEmails(xmlUser.getEmail(), "City '" + cityRef + "' is not present in DB"));
                failedEmails.add(xmlUser.getEmail());
            } else if (!groupRefs.isEmpty() && !groups.keySet().containsAll(groupRefs)) {
                failed.add(new FailedEmails(xmlUser.getEmail(), "Not all groups (" + groupRefs + ") are exist in DB"));
                failedEmails.add(xmlUser.getEmail());
            } else {
                final User user = new User(userId++, xmlUser.getValue(), xmlUser.getEmail(), UserFlag.valueOf(xmlUser.getFlag().value()), cityRef);
                userChunk.add(user);
                for (String groupRef: groupRefs) {
                    final UserGroup userGroup = new UserGroup(user.getId(), groups.get(groupRef).getId());
                    allNewUserGroups.add(userGroup);
                }
                allNewUserEmails.put(user.getId(), user.getEmail());

                if (userChunk.size() == chunkSize) {
                    addChunkFutures(chunkFutures, userChunk);
                    userChunk = new ArrayList<>(chunkSize);
                    userId = userDao.getSeqAndSkip(chunkSize);
                }
            }
        }

        if (!userChunk.isEmpty()) {
            addChunkFutures(chunkFutures, userChunk);
        }

        List<String> allAlreadyPresents = new ArrayList<>();
        chunkFutures.forEach((emailRange, future) -> {
            try {
                List<String> alreadyPresentsInChunk = future.get();
                log.info("{} successfully executed with already presents: {}", emailRange, alreadyPresentsInChunk);
                allAlreadyPresents.addAll(alreadyPresentsInChunk);
            } catch (InterruptedException | ExecutionException e) {
                log.error(emailRange + " failed", e);
                failed.add(new FailedEmails(emailRange, e.toString()));

            }
        });

        List<UserGroup> userGroupsToDB = new ArrayList<>();

        for (int i = 0; i < allNewUserGroups.size(); i++) {
            UserGroup userGroup = allNewUserGroups.get(i);
            String email = allNewUserEmails.get(userGroup.getUserId());
            if (!allAlreadyPresents.isEmpty() && allAlreadyPresents.contains(email)) {
                continue;
            }

            if (!failedEmails.isEmpty() && failedEmails.contains(email)) {
                continue;
            }

            boolean isItFailed = failed.stream()
                    .filter(failedEmailsRange -> failedEmailsRange.emailsOrRange.contains("-"))
                    .map(failedEmailsRange -> failedEmailsRange.emailsOrRange.split("-")[0].replaceFirst("\\[", "").trim())
                    .anyMatch(email::equals);

            if (isItFailed) {
                i = i + chunkSize - 1;
                continue;
            }

            userGroupsToDB.add(userGroup);
        }

        userGroupDao.insertBatch(userGroupsToDB);

        if (!allAlreadyPresents.isEmpty()) {
            failed.add(new FailedEmails(allAlreadyPresents.toString(), "already presents"));
        }

        return failed;
    }

    private void addChunkFutures(Map<String, Future<List<String>>> chunkFutures, List<User> chunk) {
        String emailRange = String.format("[%s-%s]", chunk.get(0).getEmail(), chunk.get(chunk.size() - 1).getEmail());
        Future<List<String>> future = executorService.submit(() -> userDao.insertAndGetConflictEmails(chunk));
        chunkFutures.put(emailRange, future);
        log.info("Submit chunk: " + emailRange);
    }

}

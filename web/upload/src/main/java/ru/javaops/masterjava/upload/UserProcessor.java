package ru.javaops.masterjava.upload;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;
import ru.javaops.masterjava.to.UploadTo;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.JaxbUnmarshaller;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

public class UserProcessor {
    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
    private static final UserDao dao = DBIProvider.getDao(UserDao.class);
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public List<UploadTo> process(final InputStream is, int chunkSize) throws XMLStreamException, JAXBException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);
        JaxbUnmarshaller unmarshaller = jaxbParser.createUnmarshaller();

        List<UploadTo> result = new ArrayList<>();
        List<User> currentChunkUsers = new ArrayList<>(chunkSize);

        int chunkNumber = 1;

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {

            if (currentChunkUsers.size() == chunkSize) {
                addTo(result, currentChunkUsers, chunkNumber);
                chunkNumber++;
            }

            ru.javaops.masterjava.xml.schema.User xmlUser = unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);
            User user = new User(xmlUser.getValue(), xmlUser.getEmail(), UserFlag.valueOf(xmlUser.getFlag().value()));
            currentChunkUsers.add(user);
        }

        //last chunk
        if (!currentChunkUsers.isEmpty()) {
            addTo(result, currentChunkUsers, chunkNumber);
        }

        return result;
    }

    private void addTo(List<UploadTo> result, List<User> currentChunkUsers, int chunkNumber) {
        String firstEmail = currentChunkUsers.get(0).getEmail();
        String lastEmail = currentChunkUsers.get(currentChunkUsers.size() - 1).getEmail();

        UploadTo to;
        try {
            List <String> existingUserEmails = executor.submit(() -> insertChunk(currentChunkUsers)).get();
            to = new UploadTo(chunkNumber, firstEmail, lastEmail, existingUserEmails);
        } catch (InterruptedException | ExecutionException e) {
            to = new UploadTo(chunkNumber, firstEmail, lastEmail, e.getMessage());
        }

        result.add(to);
        currentChunkUsers.clear();
    }

    private List<String> insertChunk(List<User> currentChunkUsers) {
        final int nextSeqNumber = dao.getCurrentSeq();

        IntStream.range(0, currentChunkUsers.size())
                .forEach(i -> currentChunkUsers.get(i).setId(i + nextSeqNumber));

        List<Integer> userIds = dao.insertChunkWithId(currentChunkUsers);

        return currentChunkUsers.stream()
                .filter(user -> !userIds.contains(user.getId()))
                .map(User::getEmail)
                .collect(Collectors.toList());
    }
}

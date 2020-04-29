package ru.javaops.masterjava.upload;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.persist.model.type.GroupType;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

@Slf4j
public class ProjectGroupProcessor {
    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
    private final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);
    private final ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);

    public Map<String, Group> process(StaxStreamProcessor processor) throws XMLStreamException, JAXBException {
        val unmarshaller = jaxbParser.createUnmarshaller();
        val projectMap = projectDao.getAsMap();
        val groupMap = groupDao.getAsMap();

        while (processor.startElement("Project", "Projects")) {
            ru.javaops.masterjava.xml.schema.Project xmlProject =
                    unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.Project.class);

            if (!projectMap.containsKey(xmlProject.getName())) {
                Project project = new Project(xmlProject.getName(), xmlProject.getDescription());
                log.info("Insert project: " + project);
                int id = projectDao.insertGeneratedId(project);
                project.setId(id);
                projectMap.put(project.getName(), project);
            }

            List<ru.javaops.masterjava.xml.schema.Project.Group> xmlGroups = xmlProject.getGroup();

            for (ru.javaops.masterjava.xml.schema.Project.Group xmlGroup: xmlGroups) {
                if (!groupMap.containsKey(xmlGroup.getName())) {
                    Group group = new Group(
                            xmlGroup.getName(),
                            GroupType.valueOf(xmlGroup.getType().name().toUpperCase()),
                            projectMap.get(xmlProject.getName()).getId()
                    );
                    log.info("Insert group: " + group);
                    groupDao.insert(group);
                }
            }

        }

        return groupDao.getAsMap();
    }
}

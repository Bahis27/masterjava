package ru.javaops.masterjava.service;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import com.google.common.io.Resources;

import ru.javaops.masterjava.xml.schema.Group;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;

public class MainXml {

    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

    public static void main(String[] args) {
        try {
            printUsers("masterjava02");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printUsers(String groupName) throws JAXBException, IOException {
        Payload payload = JAXB_PARSER.unmarshal(Resources.getResource("payload.xml").openStream());
        Group group = payload.getGroups().getGroup().stream()
                .filter(g -> g.getGroupName().equals(groupName))
                .findAny()
                .orElse(null);

        if (group != null) {
            group.getUsers().getUser().stream()
                    .map(User::getFullName)
                    .sorted(String::compareTo)
                    .collect(Collectors.toList())
                    .forEach(System.out::println);
        } else {
            System.out.println("There is no any group with " + groupName + " name.");
        }
    }
}

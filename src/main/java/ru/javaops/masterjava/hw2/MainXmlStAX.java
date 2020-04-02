package ru.javaops.masterjava.hw2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import com.google.common.io.Resources;

import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

public class MainXmlStAX {

    public static void main(String[] args) {
        try {
            printUsersWithEmails("masterjava02");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printUsersWithEmails(String name) throws IOException, XMLStreamException {
        try (StaxStreamProcessor processor =
                new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {

            XMLStreamReader reader = processor.getReader();
            int count = 0;
            List<String> result = new ArrayList<>();

            String groupName;
            while ((groupName = processor.getElementValue("groupName")) != null) {

                if (groupName.equals(name)) {
                    count++;
                    int event = 0;

                    String email = null;

                    while (reader.hasNext()) {
                        if (event == XMLEvent.END_ELEMENT) {
                            if (reader.getLocalName().equals("Users")) {
                                break;
                            }
                        }

                        event = reader.next();
                        if (event == XMLEvent.START_ELEMENT) {

                            if (reader.getLocalName().equals("User")) {
                                email = reader.getAttributeValue(2);
                            }

                            if (reader.getLocalName().equals("fullName")) {
                                if (reader.hasNext()) {
                                    reader.next();
                                    result.add(reader.getText() + " - " + email);
                                }
                            }
                        }
                    }
                }
            }

            if (count == 0) {
                System.out.println("There is no any group with " + name + " name.");
            } else {
                result.stream()
                        .sorted()
                        .forEach(System.out::println);
            }
        }
    }
}

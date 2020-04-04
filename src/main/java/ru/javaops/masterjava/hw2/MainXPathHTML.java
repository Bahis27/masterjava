package ru.javaops.masterjava.hw2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.w3c.dom.NodeList;

import com.google.common.io.Resources;

import ru.javaops.masterjava.xml.util.XPathProcessor;

public class MainXPathHTML {

    public static void main(String[] args) {
        try (InputStream is =
                     Resources.getResource("payload.xml").openStream()) {
            XPathProcessor processor = new XPathProcessor(is);
            String groupName = "masterjava02";

            Map<String, String> users = getUsers(processor, groupName);

            if (users == null) {
                System.out.println("There is no any group with " + groupName + " name.");
            } else {
                generateHTML(users, groupName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> getUsers(XPathProcessor processor, String groupName) {

        XPathExpression expression =
                XPathProcessor.getExpression(
                        "//*[text() = '" + groupName + "']/../*/*[name()='User']"
                );

        NodeList nodes = processor.evaluate(expression, XPathConstants.NODESET);

        if (nodes.getLength() == 0) {
            return null;
        }

        Map<String, String> users = new TreeMap<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            String email = nodes.item(i).getAttributes().getNamedItem("email").getNodeValue();
            String name = processor.evaluate(
                    XPathProcessor.getExpression(
                            "//*[text() = '" + groupName + "']/../*/*[name()='User'][" + (i + 1) + "]/*[name()='fullName']/text()"
                    ),
                    XPathConstants.STRING
            );

            users.put(name, email);
        }

        return users;
    }

    private static void generateHTML(Map<String, String> users, String groupName) {
        try (FileWriter out = new FileWriter(new File("users.html"))){
            out.write("<!DOCTYPE html>\n" +
                    "<html lang=\"ru\">\n" +
                    "<head>\n" +
                    "  <title>Пользователи</title>\n" +
                    "  <meta charset=\"utf-8\"/>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "  <h1>Пользователи группы " + groupName + "</h1>\n" +
                    "  <table border=\"1\" cellpadding=\"10\" cellspacing=\"0\">\n" +
                    "    <thead>\n" +
                    "      <tr>\n" +
                    "        <th align=\"left\">ИМЯ</th>\n" +
                    "        <th align=\"left\">ПОЧТОВЫЙ ЯЩИК</th>\n" +
                    "      </tr>\n" +
                    "    </thead>\n" +
                    "    <tbody>");

            users.forEach((k, v) -> {
                try {
                    out.write("<tr>\n" +
                            "        <td>" + k + "</td>\n" +
                            "        <td>" + v + "</td>\n" +
                            "      </tr>");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            out.write("    </tbody>\n" +
                    "  </table>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

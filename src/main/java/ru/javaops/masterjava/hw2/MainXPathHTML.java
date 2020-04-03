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

            generateHTML("masterjava02", processor);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateHTML(String groupName, XPathProcessor processor) {
        int projectQuantity = getQuantity("count(/*[name()='Payload']/*[name()='Projects']/*[name()='Project'])", processor);

        int projectCount;
        int groupCount = 0;

        boolean isExist = false;

        out:
        for (projectCount = 1; projectCount <= projectQuantity; projectCount++) {

            int groupQuantity = getQuantity(
                    "count(/*[name()='Payload']/*[name()='Projects']/*[name()='Project'][" + projectCount +
                            "]/*[name()='Groups']/*[name()='Group']/*[name()='groupName'])",
                    processor);

            for (groupCount = 1; groupCount <= groupQuantity; groupCount++) {
                XPathExpression expression = XPathProcessor.getExpression(
                        "/*[name()='Payload']/*[name()='Projects']/*[name()='Project'][" + projectCount +
                                "]/*[name()='Groups']/*[name()='Group'][" + groupCount + "]/*[name()='groupName']/text()");
                String currentGroupName = processor.evaluate(expression, XPathConstants.STRING);
                if (currentGroupName.equals(groupName)) {
                    isExist = true;
                    break out;
                }
            }
        }

        if (!isExist) {
            System.out.println("There is no any group with " + groupName + " name.");
            return;
        }

        writeHTML(getUsers(processor, projectCount, groupCount), groupName);

    }

    private static int getQuantity(String exp, XPathProcessor processor) {
        XPathExpression expression =
                XPathProcessor.getExpression(exp);
        Double d = processor.evaluate(expression, XPathConstants.NUMBER);
        return d.intValue();
    }

    private static Map<String, String> getUsers(XPathProcessor processor, int projectCount, int groupCount) {
        Map<String, String> users = new TreeMap<>();
        XPathExpression expression =
                XPathProcessor.getExpression(
                        "/*[name()='Payload']/*[name()='Projects']/*[name()='Project'][" + projectCount +
                                "]/*[name()='Groups']/*[name()='Group'][" + groupCount +
                                "]/*[name()='Users']/*[name()='User']"
                );

        NodeList nodes = processor.evaluate(expression, XPathConstants.NODESET);

        for (int i = 0; i < nodes.getLength(); i++) {
            String email = nodes.item(i).getAttributes().getNamedItem("email").getNodeValue();
            String name = processor.evaluate(
                    XPathProcessor.getExpression(
                            "/*[name()='Payload']/*[name()='Projects']/*[name()='Project'][" + projectCount +
                                    "]/*[name()='Groups']/*[name()='Group'][" + groupCount +
                                    "]/*[name()='Users']/*[name()='User'][" + (i + 1) + "]/*[name()='fullName']/text()"
                    ),
                    XPathConstants.STRING
            );

            users.put(name, email);
        }

        return users;
    }

    private static void writeHTML(Map<String, String> users, String groupName) {
        try (FileWriter out = new FileWriter(new File("users.html"))){
            out.write("<html lang=\"ru\">\n" +
                    "<head>\n" +
                    "  <title>Пользователи</title>\n" +
                    "  <meta charset=\"utf-8\">\n" +
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

            out.write("</body>\n" +
                    "\n" +
                    "</html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

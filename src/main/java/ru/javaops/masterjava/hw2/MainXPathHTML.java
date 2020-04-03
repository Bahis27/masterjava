package ru.javaops.masterjava.hw2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import com.google.common.io.Resources;

import ru.javaops.masterjava.xml.util.XPathProcessor;

public class MainXPathHTML {

    public static void main(String[] args) {
        try (InputStream is =
                     Resources.getResource("payload.xml").openStream()) {
            XPathProcessor processor = new XPathProcessor(is);

            String name = "masterjava02";
            int resultIndex = getResultIndex(name, processor);

            if (resultIndex == 0) {
                System.out.println("There is no any group with " + name + " name.");
            } else {
                generateHTML(getUsersWithEmail(resultIndex, processor), name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateHTML(Map<String, String> users, String groupName) {
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

    private static Map<String, String> getUsersWithEmail(int resultIndex, XPathProcessor processor) {
        String exp = "count(/*[name()='Payload']/*[name()='Groups']/*[name()='Group'][" +
                resultIndex + "]/*[name()='Users']/*[name()='User'])";

        Map<String, String> users = new HashMap<>();
        XPathExpression expression;

        for (int i = 1; i <= getQuantity(exp, processor) ; i++) {
            expression = XPathProcessor.getExpression("/*[name()='Payload']/*[name()='Groups']/*[name()='Group'][" +
                    resultIndex + "]/*[name()='Users']/*[name()='User'][" +
                    + i + "]/*[name()='fullName']/text()");
            String name = processor.evaluate(expression, XPathConstants.STRING);

            expression = XPathProcessor.getExpression("/*[name()='Payload']/*[name()='Groups']/*[name()='Group'][" +
                    resultIndex + "]/*[name()='Users']/*[name()='User'][" +
                    + i + "]/@email");
            String email = processor.evaluate(expression, XPathConstants.STRING);

            users.put(name, email);
        }

        return users;
    }

    private static int getResultIndex(String name, XPathProcessor processor) {
        int resultIndex = 0;
        String exp = "count(/*[name()='Payload']/*[name()='Groups']/*[name()='Group'])";
        XPathExpression expression;

        for (int i = 1; i <= getQuantity(exp, processor); i++) {
            expression = XPathProcessor.getExpression(
                    "/*[name()='Payload']/*[name()='Groups']/*[name()='Group'][" + i + "]/*[name()='groupName']/text()");

            String s = processor.evaluate(expression, XPathConstants.STRING);
            if (s.equals(name)) {
                resultIndex = i;
                break;
            }
        }

        return resultIndex;
    }

    private static int getQuantity(String exp, XPathProcessor processor) {
        XPathExpression expression =
                XPathProcessor.getExpression(exp);
        Double d = processor.evaluate(expression, XPathConstants.NUMBER);
        return d.intValue();
    }

}

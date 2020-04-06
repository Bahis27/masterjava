package ru.javaops.masterjava.hw2;

import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.w3c.dom.NodeList;

import com.google.common.io.Resources;

import j2html.tags.ContainerTag;
import ru.javaops.masterjava.xml.util.XPathProcessor;

import static j2html.TagCreator.body;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.meta;
import static j2html.TagCreator.table;
import static j2html.TagCreator.td;
import static j2html.TagCreator.th;
import static j2html.TagCreator.title;
import static j2html.TagCreator.tr;

public class MainXPathHTML {

    public static void main(String[] args) {
        try (InputStream is = Resources.getResource("payload.xml").openStream();
             Writer out = Files.newBufferedWriter(Paths.get("out/users.html"))) {

            XPathProcessor processor = new XPathProcessor(is);
            String groupName = "masterjava02";

            Map<String, String> users = getUsers(processor, groupName);

            if (users == null) {
                System.out.println("There is no any group with " + groupName + " name.");
            } else {
                out.write(toHTML(users, groupName));
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

    private static String toHTML(Map<String, String> users, String groupName) {
        final ContainerTag table = table().with(
                tr().with(th("ИМЯ"), th("ПОЧТОВЫЙ ЯЩИК")))
                .attr("border", "1")
                .attr("cellpadding", "10")
                .attr("cellspacing", "0");

        users.forEach((k, v) -> {
            table.with(tr().with(td(k), td(v)));
        });

        return html().with(
                head().with(title("Пользователи"), meta().withCharset("utf-8")),
                body().with(h1("Пользователи группы " + groupName), table)
        ).render();
    }
}

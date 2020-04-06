package ru.javaops.masterjava.hw2;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.transform.TransformerException;

import com.google.common.io.Resources;

import ru.javaops.masterjava.xml.util.XsltProcessor;

public class MainXsltHTML {

    public static void main(String[] args) {
        try {
            generateHTML("masterjava");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateHTML(String projectName) throws IOException, TransformerException {

        try (InputStream xslIn = Resources.getResource("groups.xsl").openStream();
             InputStream xmlIn = Resources.getResource("payload.xml").openStream();
             Writer out = Files.newBufferedWriter(Paths.get("out/group.html"))) {

            XsltProcessor processor = new XsltProcessor(xslIn);
            processor.setParameter("projectName", projectName);

            out.write(processor.transform(xmlIn));
        }
    }
}

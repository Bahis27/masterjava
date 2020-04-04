package ru.javaops.masterjava.hw2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

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
             FileWriter out = new FileWriter(new File("group.html"))) {

            XsltProcessor processor = new XsltProcessor(xslIn);
            processor.setParameter("projectName", projectName);

            out.write(processor.transform(xmlIn));
        }
    }
}

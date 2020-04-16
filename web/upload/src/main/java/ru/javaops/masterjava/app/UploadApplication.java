package ru.javaops.masterjava.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.thymeleaf.context.WebContext;

import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

public class UploadApplication {
    public void showForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale());
        ctx.setVariable("message", "*.xml");
        ThymeleafAppUtil.getTemplateEngine(request.getServletContext()).process("welcome", ctx, response.getWriter());
    }

    public void processFile(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, XMLStreamException {
        WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale());

        if (ServletFileUpload.isMultipartContent(request)) {

            Collection<Part> parts = request.getParts();
            List<User> users = new ArrayList<>();

            for (Part part : parts) {
                try (StaxStreamProcessor processor = new StaxStreamProcessor(part.getInputStream());
                     JaxbParser parser = JaxbParser.getInstance(ObjectFactory.class)) {

                    while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                        User user = parser.unmarshal(processor.getReader(), User.class);
                        users.add(user);
                    }

                } catch (JAXBException e) {
                    e.printStackTrace();
                }
            }

            ctx.setVariable("users", users);
            ThymeleafAppUtil.getTemplateEngine(request.getServletContext()).process("users", ctx, response.getWriter());

        } else {
            ctx.setVariable("message", "CORRECT *.xml");
            ThymeleafAppUtil.getTemplateEngine(request.getServletContext()).process("welcome", ctx, response.getWriter());
        }
    }
}

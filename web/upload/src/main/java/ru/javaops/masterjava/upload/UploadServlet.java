package ru.javaops.masterjava.upload;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.persist.model.City;

import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;

@WebServlet(urlPatterns = "/", loadOnStartup = 1)
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10) //10 MB in memory limit
@Slf4j
public class UploadServlet extends HttpServlet {
    private static final int CHUNK_SIZE = 2000;

    private final UserProcessor userProcessor = new UserProcessor();
    private final CityProcessor cityProcessor = new CityProcessor();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        out(req, resp, "", CHUNK_SIZE);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String message;
        int chunkSize = CHUNK_SIZE;
        try {
//            http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html
            chunkSize = Integer.parseInt(req.getParameter("chunkSize"));
            if (chunkSize < 1) {
                message = "Chunk Size must be > 1";
            } else {

                boolean isCityUploaded = false;
                boolean isUserUploaded = false;
                List<City> added = null;
                List<UserProcessor.FailedEmails> failed = null;

                Part filePart = req.getPart("citiesToUpload");
                if (filePart.getSize() != 0) {
                    isCityUploaded = true;
                    try (InputStream is = filePart.getInputStream()) {
                        added = cityProcessor.process(is, chunkSize);
                        log.info("Added cities: " + added);
                    }
                }

                filePart = req.getPart("usersToUpload");
                if (filePart.getSize() != 0) {
                    isUserUploaded = true;
                    try (InputStream is = filePart.getInputStream()) {
                        failed = userProcessor.process(is, chunkSize);
                        log.info("Failed users: " + failed);
                    }
                }

                if (!isCityUploaded && !isUserUploaded) {
                    message = "Specify almost one file";
                } else {
                    message = "Done";
                    final WebContext webContext =
                            new WebContext(req, resp, req.getServletContext(), req.getLocale(),
                                    ImmutableMap.of(
                                            "cities", added != null ? added : Collections.emptyList(),
                                            "users", failed != null ? failed : Collections.emptyList(),
                                            "isCityUploaded" , isCityUploaded,
                                            "isUserUploaded", isUserUploaded
                                    ));
                    engine.process("result", webContext, resp.getWriter());
                    return;
                }
            }

        } catch (Exception e) {
            log.info(e.getMessage(), e);
            message = e.toString();
        }
        out(req, resp, message, chunkSize);
    }

    private void out(HttpServletRequest req, HttpServletResponse resp, String message, int chunkSize) throws IOException {
        resp.setCharacterEncoding("utf-8");
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale(),
                ImmutableMap.of("message", message, "chunkSize", chunkSize));
        engine.process("upload", webContext, resp.getWriter());
    }
}

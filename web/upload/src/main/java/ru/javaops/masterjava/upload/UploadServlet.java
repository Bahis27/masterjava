package ru.javaops.masterjava.upload;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.WebContext;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;

@WebServlet(urlPatterns = "/", loadOnStartup = 1)
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10) //10 MB in memory limit
public class UploadServlet extends HttpServlet {

    private final UserProcessor userProcessor = new UserProcessor();
    private static final int CHUNK_SIZE = 5;
    private static final Logger log = LoggerFactory.getLogger(UploadServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());
        engine.process("upload", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());

        try {
//            http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html
            Part filePart = req.getPart("fileToUpload");
            if (filePart.getSize() == 0) {
                throw new IllegalStateException("Upload file have not been selected");
            }

            int chunkSize;

            try {
                String chunkSizeStringParam = req.getParameter("chunkSize");
                if (chunkSizeStringParam.isEmpty()) {
                    chunkSize = CHUNK_SIZE;
                } else {
                    chunkSize = Integer.parseInt(chunkSizeStringParam);
                }

            } catch (NumberFormatException e) {
                throw new IllegalStateException("Wrong chunkSize param");
            }

            try (InputStream is = filePart.getInputStream()) {
                List<User> users = userProcessor.process(is);

                UserDao dao = DBIProvider.getDao(UserDao.class);
                webContext.setVariable("users", dao.insertAll(users, chunkSize));
                engine.process("result", webContext, resp.getWriter());
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            webContext.setVariable("exception", e);
            engine.process("exception", webContext, resp.getWriter());
        }
    }
}

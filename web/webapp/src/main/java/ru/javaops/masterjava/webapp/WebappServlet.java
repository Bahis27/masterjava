package ru.javaops.masterjava.webapp;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.WebContext;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;

@WebServlet(urlPatterns = "/", loadOnStartup = 1)
public class WebappServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(WebappServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext  = new WebContext(req, resp, req.getServletContext(), req.getLocale());

        try {
            UserDao dao = DBIProvider.getDao(UserDao.class);
            List<User> firs20users = dao.getWithLimit(20);

            webContext.setVariable("users", firs20users);
            engine.process("first", webContext, resp.getWriter());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            webContext.setVariable("exception", e);
            engine.process("exception", webContext, resp.getWriter());
        }
    }
}

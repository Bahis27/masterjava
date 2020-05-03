package ru.javaops.masterjava.webapp;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.context.WebContext;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.service.mail.Addressee;
import ru.javaops.masterjava.service.mail.MailWSClient;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;

@Slf4j
@WebServlet("")
public class UsersServlet extends HttpServlet {
    private UserDao userDao = DBIProvider.getDao(UserDao.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale(),
                ImmutableMap.of("users", userDao.getWithLimit(20)));
        engine.process("users", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String message;
        try {
            String[] addressees = req.getParameterMap().get("checkBox");
            if (addressees == null) {
                message = "Check someone";
            } else {
                MailWSClient.sendToGroup(
                        Arrays.stream(addressees).map(Addressee::new).collect(ImmutableSet.toImmutableSet()),
                        Collections.emptySet(),
                        req.getParameter("subject"),
                        req.getParameter("messageBody")
                );
                log.info("Send to " + addressees);
                message = "Done";
            }
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            message = e.toString();
        }

        final WebContext webContext = new WebContext(
                req, resp, req.getServletContext(), req.getLocale(),
                ImmutableMap.of("message", message)
        );
        resp.setCharacterEncoding("utf-8");
        engine.process("users", webContext, resp.getWriter());
    }
}

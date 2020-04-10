package ru.javaops.masterjava.app;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.context.WebContext;

public class WelcomeApplication {
    public void process(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale());
        ctx.setVariable("currentDate", new Date());

        ThymeleafAppUtil.getTemplateEngine(request.getServletContext()).process("welcome", ctx, response.getWriter());
    }
}

package ru.javaops.masterjava.service.mail;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.web.WebStateException;

@Slf4j
public class MailWSClientMain {
    public static void main(String[] args) throws WebStateException {
        String state = MailWSClient.sendToGroup(
                ImmutableSet.of(new Addressee("To <pablo.alejo@yandex.ru>")),
                ImmutableSet.of(new Addressee("Copy <dude27@yandex.ru>")),
                "web_html_page", "Получил или нет?",
                ImmutableList.of(new Attachment("version.html", new DataHandler(new FileDataSource("config_templates/version.html"))))
                );
        System.out.println(state);
    }
}
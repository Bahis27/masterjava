package ru.javaops.masterjava.service.mail;

import javax.xml.ws.Endpoint;

import ru.javaops.masterjava.persist.dao.AbstractDaoTest;
import ru.javaops.masterjava.service.mail.dao.MailSendResultDao;

/**
 * User: gkislin
 * Date: 28.05.2014
 */
public class MailServicePublisher extends AbstractDaoTest<MailSendResultDao> {

    public MailServicePublisher() {
        super(MailSendResultDao.class);
    }

    public static void main(String[] args) {
        Endpoint.publish("http://localhost:8080/mail/mailService", new MailServiceImpl());
    }
}

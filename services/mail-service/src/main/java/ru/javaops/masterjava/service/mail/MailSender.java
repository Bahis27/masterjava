package ru.javaops.masterjava.service.mail;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import com.typesafe.config.Config;

import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.service.mail.dao.MailSendResultDao;
import ru.javaops.masterjava.service.mail.model.MailSendResult;

@Slf4j
public class MailSender {

    private static Email emailTemplate = getEmailTemplate();
    private static MailSendResultDao dao = DBIProvider.getDao(MailSendResultDao.class);

    private static Email getEmailTemplate() {

        Email email = null;

        try {
            email = new SimpleEmail();
            Config mailConfig = Configs.getConfig("mail.conf", "mail");
            email.setHostName(mailConfig.getString("host"));
            email.setSmtpPort(mailConfig.getInt("port"));
            email.setAuthentication(mailConfig.getString("fromName"), mailConfig.getString("password"));
            email.setSSLOnConnect(mailConfig.getBoolean("useSSL"));
            email.setStartTLSEnabled(mailConfig.getBoolean("useTLS"));
            email.setDebug(mailConfig.getBoolean("debug"));
            email.setFrom(mailConfig.getString("username"));
        } catch (EmailException e) {
            log.error(e.getMessage(), e);
        }

        return email;
    }

    static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        log.info("Send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));
        try {
            if (emailTemplate != null) {
                emailTemplate.setMsg(body);
                emailTemplate.setSubject(subject);
                addTOAddressee(emailTemplate, to);
                addCCAddressee(emailTemplate, cc);
                emailTemplate.send();
                dao.insertGeneratedId(new MailSendResult(
                        Arrays.toString(to.toArray()) + "| copy: |" + Arrays.toString(cc.toArray()),
                        subject,
                        body,
                        "success"
                ));
            }
        } catch (EmailException e) {
            dao.insertGeneratedId(new MailSendResult(
                    Arrays.toString(to.toArray()) + "| copy: |" + Arrays.toString(cc.toArray()),
                    subject,
                    body,
                    "failed: " + e.getMessage()
            ));
            log.error(e.getMessage(), e);
        }
    }

    private static void addTOAddressee(Email emailTemplate, List<Addressee> to) throws EmailException {
        for (Addressee addr: to) {
            emailTemplate.addTo(addr.getEmail(), addr.getName());
        }
    }

    private static void addCCAddressee(Email emailTemplate, List<Addressee> cc) throws EmailException {
        for (Addressee addr: cc) {
            emailTemplate.addCc(addr.getEmail(), addr.getName());
        }
    }
}

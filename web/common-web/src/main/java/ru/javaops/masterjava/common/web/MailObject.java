package ru.javaops.masterjava.common.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.io.IOUtils;

public class MailObject implements Serializable {
    private String users;
    private String subject;
    private String body;
    private String attachName;
    private byte[] attachment;

    public MailObject(String users, String subject, String body, String attachName, InputStream inputStream) throws IOException {
        this.users = users;
        this.subject = subject;
        this.body = body;
        this.attachName = attachName;
        setAttachment(inputStream);
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAttachName() {
        return attachName;
    }

    public void setAttachName(String attachName) {
        this.attachName = attachName;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(InputStream inputStream) throws IOException {
        this.attachment = inputStream == null ? null : IOUtils.toByteArray(inputStream);
    }

    @Override
    public String toString() {
        return "MailObject{" +
                "users='" + users + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", fileName='" + attachName + '\'' +
                '}';
    }
}

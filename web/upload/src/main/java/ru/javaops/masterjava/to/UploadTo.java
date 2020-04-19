package ru.javaops.masterjava.to;

import java.util.List;

public class UploadTo {

    private int chunkNumber;

    private String startEmail;

    private String lastEmail;

    private List<String> existingUserEmails;

    private String exceptionCause;

    public UploadTo(int chunkNumber, String startEmail, String lastEmail, List<String> existingUserEmails) {
        this.chunkNumber = chunkNumber;
        this.startEmail = startEmail;
        this.lastEmail = lastEmail;
        this.existingUserEmails = existingUserEmails;
    }

    public UploadTo(int chunkNumber, String startEmail, String lastEmail, String exceptionCause) {
        this.chunkNumber = chunkNumber;
        this.startEmail = startEmail;
        this.lastEmail = lastEmail;
        this.exceptionCause = exceptionCause;
    }

    public boolean isSuccessful() {
        return this.exceptionCause == null;
    }

    public boolean isFullySuccessful() {
        if (!isSuccessful()) {
            return false;
        }
        return this.existingUserEmails.isEmpty();
    }

    public int getChunkNumber() {
        return chunkNumber;
    }

    public String getStartEmail() {
        return startEmail;
    }

    public String getLastEmail() {
        return lastEmail;
    }

    public List<String> getExistingUserEmails() {
        return existingUserEmails;
    }

    public String getExceptionMessage() {
        return exceptionCause;
    }
}

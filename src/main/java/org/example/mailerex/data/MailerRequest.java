package org.example.mailerex.data;

import java.util.List;

/**
 * Represents an email to be sent.
 * <p>
 * Created by robertb on 31/1/20.
 */
public class MailerRequest {

    private List<String> toAddresses;
    private List<String> ccAddresses;
    private List<String> bccAddresses;
    private String body;

    // TODO: html, mime types, attachments

    public List<String> getToAddresses() {
        return toAddresses;
    }

    public void setToAddresses(List<String> toAddresses) {
        this.toAddresses = toAddresses;
    }

    public List<String> getCcAddresses() {
        return ccAddresses;
    }

    public void setCcAddresses(List<String> ccAddresses) {
        this.ccAddresses = ccAddresses;
    }

    public List<String> getBccAddresses() {
        return bccAddresses;
    }

    public void setBccAddresses(List<String> bccAddresses) {
        this.bccAddresses = bccAddresses;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

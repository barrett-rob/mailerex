package org.example.mailerex.mailer.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.example.mailerex.data.MailerRequest;
import org.example.mailerex.mailer.Mailer;
import org.example.mailerex.services.ConfigurationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sends an email via the SendGrid HTTP API.
 *
 * <p>
 * Created by robertb on 31/1/20.
 */
public class SendGridMailer extends BaseHttpMailer implements Mailer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public SendGridMailer() {
        super(new ConfigurationService()); // should be injected
    }

    public static class SendGridMailerMessage {
        public final List<Personalization> personalizations = new ArrayList<>();
        public Email from;
        public String subject;
        public final List<Content> content = new ArrayList<>();
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Personalization {
        public final List<Email> to = new ArrayList<>();
        public final List<Email> cc = new ArrayList<>();
        public final List<Email> bcc = new ArrayList<>();
    }

    public static class Email {
        public String email;

        public Email(String email) {
            this.email = email;
        }
    }

    public static class Content {
        public String type;
        public String value;

        public Content(String type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    @Override
    protected void sendMailViaHTTP(MailerRequest mailerRequest) {
        try {
            HttpPost httpPost = new HttpPost(configurationService.getSendGridBaseUrl());
            httpPost.addHeader(HttpHeaders.AUTHORIZATION, buildAuthorizationHeaderValue());
            httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
            final SendGridMailerMessage sendGridMailerMessage = new SendGridMailerMessage();
            sendGridMailerMessage.from = new Email(mailerRequest.getFromAddress());
            final Personalization personalization = new Personalization();
            addAddresses(personalization.to, mailerRequest.getToAddresses());
            addAddresses(personalization.cc, mailerRequest.getCcAddresses());
            addAddresses(personalization.bcc, mailerRequest.getBccAddresses());
            sendGridMailerMessage.personalizations.add(personalization);
            sendGridMailerMessage.subject = mailerRequest.getSubject();
            sendGridMailerMessage.content.add(new Content("text/plain", mailerRequest.getBody()));

            final String sendgridMailerMessageAsJson = objectMapper.writeValueAsString(sendGridMailerMessage);
            httpPost.setEntity(new StringEntity(sendgridMailerMessageAsJson, StandardCharsets.UTF_8));
            final String responseBody = executeHttpRequest(httpPost);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void addAddresses(List<Email> emails, List<String> addresses) {
        if (addresses != null) {
            emails.addAll(
                    addresses
                            .stream()
                            .map(Email::new)
                            .collect(Collectors.toList()));
        }
    }

    private String buildAuthorizationHeaderValue() {
        return String.format("Bearer %s", configurationService.getSendGridApiKey());
    }
}

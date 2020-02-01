package org.example.mailerex.mailer.impl;

import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.example.mailerex.data.MailerRequest;
import org.example.mailerex.mailer.Mailer;
import org.example.mailerex.services.ConfigurationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Sends an email via the MailGun HTTP API.
 * <p>
 * Created by robertb on 31/1/20.
 */
public class MailGunMailer extends BaseHttpMailer implements Mailer {

    public MailGunMailer() {
        super(new ConfigurationService()); // should be injected
    }

    @Override
    protected void sendMailViaHTTP(MailerRequest mailerRequest) {
        try {
            HttpPost httpPost = new HttpPost(configurationService.getMailGunBaseUrl());
            httpPost.addHeader(HttpHeaders.AUTHORIZATION, buildAuthorizationHeaderValue());

            List<NameValuePair> formData = new ArrayList<>();
            formData.add(new BasicNameValuePair("from", mailerRequest.getFromAddress()));
            final List<String> toAddresses = mailerRequest.getToAddresses();
            formData.add(new BasicNameValuePair("to", String.join(",", toAddresses)));
            final List<String> ccAddresses = mailerRequest.getCcAddresses();
            if (ccAddresses != null && !ccAddresses.isEmpty()) {
                formData.add(new BasicNameValuePair("cc", String.join(",", ccAddresses)));
            }
            final List<String> bccAddresses = mailerRequest.getBccAddresses();
            if (bccAddresses != null && !bccAddresses.isEmpty()) {
                formData.add(new BasicNameValuePair("bcc", String.join(",", bccAddresses)));
            }
            formData.add(new BasicNameValuePair("subject", mailerRequest.getSubject()));
            formData.add(new BasicNameValuePair("text", mailerRequest.getBody()));

            httpPost.setEntity(new UrlEncodedFormEntity(formData, StandardCharsets.UTF_8));

            executeHttpRequest(httpPost);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildAuthorizationHeaderValue() {
        final String unencoded = "api:".concat(configurationService.getMailGunApiKey());
        final String encoded = Base64.getEncoder().encodeToString(unencoded.getBytes(StandardCharsets.US_ASCII));
        return String.format("Basic %s", encoded);
    }
}

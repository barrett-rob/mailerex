package org.example.mailerex.mailer.impl;

import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.example.mailerex.data.MailerRequest;
import org.example.mailerex.data.MailerResponse;
import org.example.mailerex.mailer.Mailer;
import org.example.mailerex.services.ConfigurationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Sends an email via the MailGun API.
 * <p>
 * Created by robertb on 31/1/20.
 */
public class MailGunMailer implements Mailer {

    private final CloseableHttpClient closeableHttpClient; // threadsafe

    private final ConfigurationService configurationService;

    public MailGunMailer(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        this.closeableHttpClient = HttpClientBuilder.create().build();
    }

    public MailGunMailer() {
        this(
                new ConfigurationService() // should be injected
        );
    }

    @Override
    public MailerResponse send(MailerRequest mailerRequest) {
        final MailerResponse mailerResponse = new MailerResponse();
        mailerResponse.setMailer(getName());
        sendMailViaMailGunApi(mailerRequest);
        mailerResponse.setMessage("mail sent");
        return mailerResponse;
    }

    private void sendMailViaMailGunApi(MailerRequest mailerRequest) {
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

            try (final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost)) {
                final HttpEntity entity = closeableHttpResponse.getEntity();
                final String responseBody = EntityUtils.toString(entity);
                final StatusLine statusLine = closeableHttpResponse.getStatusLine();
                final int statusCode = statusLine.getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    throw new IllegalStateException(
                            String.format(
                                    "email send failed: %s %s",
                                    statusLine,
                                    responseBody));
                }
            }
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

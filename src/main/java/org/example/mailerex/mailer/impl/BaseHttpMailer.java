package org.example.mailerex.mailer.impl;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.example.mailerex.data.MailerRequest;
import org.example.mailerex.data.MailerResponse;
import org.example.mailerex.mailer.Mailer;
import org.example.mailerex.services.ConfigurationService;

import java.io.IOException;

/**
 * Base class for mailers that use HTTP requests to send email.
 * <p>
 * Created by robertb on 31/1/20.
 */
public abstract class BaseHttpMailer implements Mailer {

    protected final CloseableHttpClient closeableHttpClient; // threadsafe

    protected final ConfigurationService configurationService;

    public BaseHttpMailer(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        this.closeableHttpClient = HttpClientBuilder.create().build();
    }

    @Override
    public MailerResponse send(MailerRequest mailerRequest) {
        final MailerResponse mailerResponse = new MailerResponse();
        mailerResponse.setMailer(getName());
        sendMailViaHTTP(mailerRequest);
        mailerResponse.setMessage("mail sent");
        return mailerResponse;
    }

    /**
     * Subclasses to send the request specific to their backend.
     *
     * @param mailerRequest
     */
    protected abstract void sendMailViaHTTP(MailerRequest mailerRequest);


    /**
     * Execute the supplied request and throw an exception if the status code is not 200 OK
     *
     * @param httpPost
     * @return
     * @throws IOException
     */
    protected String executeHttpRequest(HttpPost httpPost) throws IOException {
        try (final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost)) {
            final HttpEntity entity = closeableHttpResponse.getEntity();
            final String responseBody = EntityUtils.toString(entity);
            final StatusLine statusLine = closeableHttpResponse.getStatusLine();
            final int statusCode = statusLine.getStatusCode();
            if (statusCode / 100 != 2) { // not ok
                throw new IllegalStateException(
                        String.format(
                                "email send failed: %s %s",
                                statusLine,
                                responseBody));
            }
            return responseBody;
        }
    }
}
package org.example.mailerex.mailer.impl;

import org.example.mailerex.data.MailerRequest;
import org.example.mailerex.data.MailerResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by robertb on 1/2/20.
 */
public class SendGridMailerTest {

    private SendGridMailer sendGridMailer;

    @Before
    public void before() {
        sendGridMailer = new SendGridMailer();
    }

    @Test
    public void testSendingMailViaSendGrid() {
        final MailerRequest mailerRequest = new MailerRequest();
        mailerRequest.setFromAddress("Rob Barrett <barrett.rob@gmail.com>");
        mailerRequest.setToAddresses(Arrays.asList("barrett.rob@gmail.com"));
        mailerRequest.setSubject("test from " + getClass());
        mailerRequest.setBody("lorem ipsum");
        final MailerResponse mailerResponse = sendGridMailer.send(mailerRequest);
        assertEquals("SendGridMailer", mailerResponse.getMailer());
        assertEquals("mail sent", mailerResponse.getMessage());
    }

}
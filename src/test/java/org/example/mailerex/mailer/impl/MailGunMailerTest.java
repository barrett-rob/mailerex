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
public class MailGunMailerTest {

    private MailGunMailer mailGunMailer;

    @Before
    public void before() {
        mailGunMailer = new MailGunMailer();
    }

    @Test
    public void testSendingMailViaMailGun() {
        final MailerRequest mailerRequest = new MailerRequest();
        mailerRequest.setFromAddress("Rob Barrett <barrett.rob@gmail.com>");
        mailerRequest.setToAddresses(Arrays.asList("barrett.rob@gmail.com"));
        mailerRequest.setSubject("test from " + getClass());
        mailerRequest.setBody("lorem ipsum");
        final MailerResponse mailerResponse = mailGunMailer.send(mailerRequest);
        assertEquals("MailGunMailer", mailerResponse.getMailer());
        assertEquals("mail sent", mailerResponse.getMessage());
    }
}
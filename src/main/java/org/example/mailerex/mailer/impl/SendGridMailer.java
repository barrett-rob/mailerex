package org.example.mailerex.mailer.impl;

import org.example.mailerex.data.MailerRequest;
import org.example.mailerex.data.MailerResponse;
import org.example.mailerex.mailer.Mailer;

/**
 * Sends an email via the SendGrid API.
 *
 * Created by robertb on 31/1/20.
 */
public class SendGridMailer implements Mailer {
    @Override
    public MailerResponse send(MailerRequest mailerRequest) {
        return null;
    }
}

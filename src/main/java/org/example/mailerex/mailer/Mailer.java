package org.example.mailerex.mailer;

import org.example.mailerex.data.MailerRequest;
import org.example.mailerex.data.MailerResponse;

/**
 * Created by robertb on 31/1/20.
 */
public interface Mailer {

    /**
     * Send the email as described in the {@link MailerRequest}.
     *
     * @param mailerRequest
     * @return a {@link MailerResponse} object.
     */
    MailerResponse send(MailerRequest mailerRequest);
}

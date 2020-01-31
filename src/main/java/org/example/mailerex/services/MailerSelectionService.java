package org.example.mailerex.services;

import org.example.mailerex.mailer.Mailer;

/**
 * The {@link MailerSelectionService} is responsible for selecting an appropriate {@link Mailer}.
 * <p>
 * For the purposes of this example we just randomly select a {@link Mailer}, but in real life
 * we would make the decision based on factors like availability, throughput, cost, etc.
 * <p>
 * Created by robertb on 31/1/20.
 */
public class MailerSelectionService {

    public Mailer selectMailer() {
        return null;
    }
}

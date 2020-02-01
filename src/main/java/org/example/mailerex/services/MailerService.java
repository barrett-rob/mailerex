package org.example.mailerex.services;

import org.example.mailerex.data.MailerRequest;
import org.example.mailerex.data.MailerResponse;
import org.example.mailerex.mailer.Mailer;

/**
 * The {@link MailerService} delegates sending of the email to the {@link org.example.mailerex.mailer.Mailer}
 * instances that are known to it.
 * <p>
 * Created by robertb on 31/1/20.
 */
public class MailerService {

    private final MailerSelectionService mailerSelectionService;

    public MailerService(MailerSelectionService mailerSelectionService) {
        this.mailerSelectionService = mailerSelectionService;
    }

    public MailerService() {
        this(new MailerSelectionService()); // should be injected
    }

    /**
     * Send mail as described in the {@link MailerRequest} using the {@link org.example.mailerex.mailer.Mailer}
     * instance selected by the {@link MailerSelectionService}.
     *
     * @param mailerRequest
     * @return
     */
    public MailerResponse process(MailerRequest mailerRequest) {
        final Mailer mailer = mailerSelectionService.selectMailer();
        final MailerResponse mailerResponse = mailer.send(mailerRequest);
        return mailerResponse;
    }
}
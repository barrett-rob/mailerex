package org.example.mailerex.services;

import org.example.mailerex.mailer.Mailer;
import org.example.mailerex.mailer.impl.MailGunMailer;
import org.example.mailerex.mailer.impl.SendGridMailer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The {@link MailerSelectionService} is responsible for selecting an appropriate {@link Mailer}.
 * <p>
 * For the purposes of this example we just randomly select a {@link Mailer}, but in real life
 * we would make the decision based on factors like availability, throughput, cost, etc.
 * <p>
 * Created by robertb on 31/1/20.
 */
public class MailerSelectionService {

    private final List<Mailer> mailers = new ArrayList<>();
    private final Random random = new Random();

    public MailerSelectionService() {
        // should be done via dependency injection
        mailers.add(new SendGridMailer());
        mailers.add(new MailGunMailer());
    }

    public Mailer selectMailer() {
        final int size = mailers.size();
        final int randomIndex = random.nextInt(size);
        return mailers.get(randomIndex);
    }
}

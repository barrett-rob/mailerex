package org.example.mailerex.services;

import org.example.mailerex.mailer.Mailer;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by robertb on 31/1/20.
 */
public class MailerSelectionServiceTest {

    private MailerSelectionService mailerSelectionService = new MailerSelectionService(); // should be injected

    @Test
    public void test0() {
        final Set<String> mailerNames = new HashSet<>();
        for (int i = 0; i < 100000; i++) {
            final Mailer mailer = mailerSelectionService.selectMailer();
            assertNotNull(mailer);
            mailerNames.add(mailer.getName());
        }
        assertEquals(2, mailerNames.size());
    }
}
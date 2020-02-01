package org.example.mailerex.services;

import org.example.mailerex.data.MailerRequest;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.fail;

/**
 * Created by robertb on 31/1/20.
 */
public class MailerRequestValidationServiceTest {

    private MailerRequestValidationService mailerRequestValidationService = new MailerRequestValidationService();

    @Test
    public void testNullArgs() {
        try {
            mailerRequestValidationService.validate(null);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
        MailerRequest mailerRequest = new MailerRequest();
        try {
            mailerRequestValidationService.validate(mailerRequest);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
        mailerRequest.setFromAddress("wilbur@example.com");
        try {
            mailerRequestValidationService.validate(mailerRequest);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
        mailerRequest.setToAddresses(Arrays.asList("orville@example.com"));
        try {
            mailerRequestValidationService.validate(mailerRequest);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testToAddresses() {
        MailerRequest mailerRequest = new MailerRequest();
        mailerRequest.setFromAddress("wilbur@example.com");
        mailerRequest.setToAddresses(Arrays.asList("bogus"));
        try {
            mailerRequestValidationService.validate(mailerRequest);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testCcAddresses() {
        MailerRequest mailerRequest = new MailerRequest();
        mailerRequest.setFromAddress("wilbur@example.com");
        mailerRequest.setToAddresses(Arrays.asList("orville@example.com"));
        mailerRequest.setCcAddresses(Arrays.asList("bogus"));
        try {
            mailerRequestValidationService.validate(mailerRequest);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testBccAddresses() {
        MailerRequest mailerRequest = new MailerRequest();
        mailerRequest.setFromAddress("wilbur@example.com");
        mailerRequest.setToAddresses(Arrays.asList("orville@example.com"));
        mailerRequest.setCcAddresses(Arrays.asList("oliver@example.com"));
        mailerRequest.setBccAddresses(Arrays.asList("bogus"));
        try {
            mailerRequestValidationService.validate(mailerRequest);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }


}
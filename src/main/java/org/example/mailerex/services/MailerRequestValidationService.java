package org.example.mailerex.services;

import org.apache.commons.lang3.StringUtils;
import org.example.mailerex.data.MailerRequest;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.List;

/**
 * Created by robertb on 31/1/20.
 */
public class MailerRequestValidationService {

    /**
     * Validate a {@link MailerRequest} object, throw {@link IllegalArgumentException} if there are probl;ems with it.
     *
     * @param mailerRequest
     */
    public void validate(MailerRequest mailerRequest) {
        if (mailerRequest == null) {
            throw new IllegalArgumentException("null request");
        }
        if (StringUtils.isBlank(mailerRequest.getFromAddress())) {
            throw new IllegalArgumentException("missing 'fromAddress' addresses");
        }
        if (mailerRequest.getToAddresses() == null || mailerRequest.getToAddresses().isEmpty()) {
            throw new IllegalArgumentException("missing 'toAddress' addresses");
        }
        if (StringUtils.isBlank(mailerRequest.getSubject())) {
            throw new IllegalArgumentException("missing subject");
        }
        if (StringUtils.isBlank(mailerRequest.getBody())) {
            throw new IllegalArgumentException("missing body");
        }
        validateAddresses(Arrays.asList(mailerRequest.getFromAddress()));
        validateAddresses(mailerRequest.getToAddresses());
        validateAddresses(mailerRequest.getCcAddresses());
        validateAddresses(mailerRequest.getBccAddresses());
    }

    private void validateAddresses(List<String> addresses) {
        if (addresses != null) {
            for (String address : addresses) {
                try {
                    InternetAddress.parse(address);
                } catch (AddressException e) {
                    throw new IllegalArgumentException(
                            String.format(
                                    "email address [%s] not valid",
                                    address));
                }
            }
        }
    }
}
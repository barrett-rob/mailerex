package org.example.mailerex.handler;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mailerex.data.MailerRequest;
import org.example.mailerex.services.MailerRequestValidationService;
import org.example.mailerex.services.MailerService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by robertb on 1/2/20.
 */
public class MailerRequestHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MailerRequestHandler mailerRequestHandler;
    private MailerRequestValidationService mockMailerRequestValidationService;
    private MailerService mockMailerService;

    @Before
    public void before() {
        mockMailerRequestValidationService = mock(MailerRequestValidationService.class);
        mockMailerService = mock(MailerService.class);
        mailerRequestHandler = new MailerRequestHandler(mockMailerRequestValidationService, mockMailerService);
    }

    @Test
    public void testNullArgs() {
        final AwsProxyResponse awsProxyResponse = mailerRequestHandler.handleRequest(null, null);
        assertEquals(500, awsProxyResponse.getStatusCode());
    }

    @Test
    public void testNonNullArgs() throws JsonProcessingException {
        final AwsProxyRequest awsProxyRequest = new AwsProxyRequest();
        AwsProxyResponse awsProxyResponse = mailerRequestHandler.handleRequest(awsProxyRequest, null);
        assertEquals(500, awsProxyResponse.getStatusCode());
        awsProxyRequest.setBody(objectMapper.writeValueAsString(new MailerRequest()));
        awsProxyResponse = mailerRequestHandler.handleRequest(awsProxyRequest, null);
        assertEquals(200, awsProxyResponse.getStatusCode());
        verify(mockMailerRequestValidationService, times(1)).validate(any(MailerRequest.class));
        verify(mockMailerService, times(1)).process(any(MailerRequest.class));
    }
}
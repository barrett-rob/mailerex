package org.example.mailerex.handler;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.example.mailerex.data.MailerRequest;
import org.example.mailerex.data.MailerResponse;
import org.example.mailerex.services.MailerRequestValidationService;
import org.example.mailerex.services.MailerService;

import java.io.IOException;

/**
 * This is the lambda entry point for the API call.
 * <p>
 * After validating the request via the {@link org.example.mailerex.services.MailerRequestValidationService},
 * the handler will delegate processing to the {@link org.example.mailerex.services.MailerService}.
 * <p>
 * Created by robertb on 31/1/20.
 */
public class RequestHandler implements com.amazonaws.services.lambda.runtime.RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final MailerRequestValidationService mailerRequestValidationService = new MailerRequestValidationService(); // should be injected
    private final MailerService mailerService = new MailerService(); // should be injected

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest awsProxyRequest, Context context) {
        try {
            final MailerRequest mailerRequest = deserializeMailerRequest(awsProxyRequest.getBody());
            mailerRequestValidationService.validate(mailerRequest);
            final MailerResponse mailerResponse = mailerService.process(mailerRequest);
            return getAwsProxyResponse(
                    HttpStatus.SC_OK,
                    serializeMailerResponse(mailerResponse),
                    ContentType.APPLICATION_JSON.getMimeType());
        } catch (IllegalArgumentException e) {
            return getAwsProxyResponse(
                    HttpStatus.SC_BAD_REQUEST,
                    e.getMessage(),
                    ContentType.TEXT_PLAIN.getMimeType());
        } catch (Exception e) {
            return getAwsProxyResponse(
                    HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    ExceptionUtils.getStackTrace(e),
                    ContentType.TEXT_PLAIN.getMimeType());
        }
    }

    private AwsProxyResponse getAwsProxyResponse(int statusCode, String body, String contentType) {
        final AwsProxyResponse awsProxyResponse = new AwsProxyResponse();
        awsProxyResponse.setStatusCode(statusCode);
        awsProxyResponse.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
        awsProxyResponse.setBody(body);
        return awsProxyResponse;
    }

    private MailerRequest deserializeMailerRequest(String body) {
        try {
            final MailerRequest mailerRequest = objectMapper.readValue(body, MailerRequest.class);
            return mailerRequest;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String serializeMailerResponse(MailerResponse mailerResponse) {
        try {
            final String serialized = objectMapper.writeValueAsString(mailerResponse);
            return serialized;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
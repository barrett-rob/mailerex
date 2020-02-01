package org.example.mailerex.handler;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
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
 * This is the lambda entry point for the API call. All lambda considerations are
 * encapsulated at this level, but the actual business logic is carried out in
 * services.
 * <p>
 * After validating the request via the {@link org.example.mailerex.services.MailerRequestValidationService},
 * the handler will delegate processing to the {@link org.example.mailerex.services.MailerService}.
 * <p>
 * Created by robertb on 31/1/20.
 */
public class MailerRequestHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final MailerRequestValidationService mailerRequestValidationService;
    private final MailerService mailerService;

    public MailerRequestHandler(MailerRequestValidationService mailerRequestValidationService, MailerService mailerService) {
        this.mailerRequestValidationService = mailerRequestValidationService;
        this.mailerService = mailerService;
    }

    public MailerRequestHandler() {
        this(
                new MailerRequestValidationService(), // should be injected
                new MailerService() // should be injected
        );
    }

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest awsProxyRequest, Context context) {
        try {
            final String awsProxyRequestBody = awsProxyRequest.getBody();
            if (StringUtils.isBlank(awsProxyRequestBody)) {
                throw new IllegalArgumentException("missing aws proxy request 'body' element");
            }
            final MailerRequest mailerRequest = deserializeMailerRequest(awsProxyRequestBody);
            mailerRequestValidationService.validate(mailerRequest);
            final MailerResponse mailerResponse = mailerService.process(mailerRequest);
            return buildAwsProxyResponse(
                    HttpStatus.SC_OK,
                    serializeMailerResponse(mailerResponse),
                    ContentType.APPLICATION_JSON.getMimeType());
        } catch (IllegalArgumentException e) {
            return buildAwsProxyResponse(
                    HttpStatus.SC_BAD_REQUEST,
                    e.getMessage(),
                    ContentType.TEXT_PLAIN.getMimeType());
        } catch (Exception e) {
            return buildAwsProxyResponse(
                    HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    ExceptionUtils.getStackTrace(e),
                    ContentType.TEXT_PLAIN.getMimeType());
        }
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

    private AwsProxyResponse buildAwsProxyResponse(int statusCode, String body, String contentType) {
        final AwsProxyResponse awsProxyResponse = new AwsProxyResponse();
        awsProxyResponse.setStatusCode(statusCode);
        awsProxyResponse.setBody(body);
        awsProxyResponse.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
        return awsProxyResponse;
    }
}
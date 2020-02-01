# Mailer Example

#### Software Engineer Challenge

This application provides an abstraction between two different email service providers. 
If one of the services goes down, the service can quickly failover to a different provider
without affecting consumers.

Providers:

- [Mailgun](https://www.mailgun.com/)
- [SendGrid](https://sendgrid.com/)

Alternative email providers can be added by implementing the 
[Mailer](/src/main/java/org/example/mailerex/mailer/Mailer.java) interface.

The solution caters for 
- multiple email recipients, 
- CCs 
- BCCs 

(There was no need to support HTML email body types.)

The solution is implemented as one RESTful API call.

- No authentication is required.
- No 3rd party client library is used to integrate with Mailgun, Sendgrid or other providers. 
- A simple HTTP client is used to handcraft HTTP requests to the email gateway services.
    - The [Apache HTTPClient](https://hc.apache.org/httpcomponents-client-ga/index.html)
      library is used for creation and execution of HTTP requests.

#### TODO: 

Non-crucial features left unimplemented.

- handling of html/rich text/mime content
- attachments
- monitoring
- retry after network failure
- load tests
- state tracking of mailer success/failure
- load shedding
- short circuiting of requests when mailer is down

----

## Code:

The implementation uses the following technologies:

- java8
- [Apache HTTPClient](https://hc.apache.org/httpcomponents-client-ga/index.html)
- MailGun API as described here: https://documentation.mailgun.com/en/latest/api-sending.html#sending
- SendGrid API as described here: https://sendgrid.com/docs/API_Reference/Web_API_v3/index.html

Note: a production implementation should make use fo dependency injection, but for
the purposes of this example the implementations are explicitly created and injected.

The two implementations for this example are:

- [SendGridMailer](/src/main/java/org/example/mailerex/mailer/impl/SendGridMailer.java)
- [MailGunMailer](/src/main/java/org/example/mailerex/mailer/impl/MailGunMailer.java)

---- 

## Tests

The following tests cover the bulk of the example implementation:

- [MailerRequestHandlerTest.java](/src/test/java/org/example/mailerex/handler/MailerRequestHandlerTest.java)
- [ConfigurationServiceTest.java](/src/test/java/org/example/mailerex/services/ConfigurationServiceTest.java)
- [MailerRequestValidationServiceTest.java](/src/test/java/org/example/mailerex/services/MailerRequestValidationServiceTest.java)
- [MailerSelectionServiceTest.java](/src/test/java/org/example/mailerex/services/MailerSelectionServiceTest.java)
- [MailGunMailerTest.java](/src/test/java/org/example/mailerex/mailer/impl/MailGunMailerTest.java)
- [SendGridMailerTest.java](/src/test/java/org/example/mailerex/mailer/impl/SendGridMailerTest.java)

---- 

## Build

The solution can be compiled as a java application. The build tool of choice in this
implementation is [Gradle](https://gradle.org/) and the build can be invoked as follows:

`./gradlew clean build test buildZip`

This will build the solution and run all the unit tests. There will be a deployable 
zip file located at `build/distributions/mailerex-1.0-SNAPSHOT.zip`

Deployment of the lambda was done manually, as was the definition of the API in the
Amazon API Gateway. 

Automated deployment was not done as part of this implementation, but the 
[AWS Serverless Application Model](https://aws.amazon.com/serverless/sam/) 
would be an appropriate choice for deployment.

----

## Deployment:
 
- the solution is deployed as a [AWS Lambda](https://aws.amazon.com/lambda/) fronted by
  an API defined in the [Amazon API Gateway](https://aws.amazon.com/api-gateway/)
- to keep this example simple the deployment was not automated
- the deployed service is available at https://j6ecw6w1j0.execute-api.ap-southeast-2.amazonaws.com/messages

The service description is pretty basic for the purposes of this example. It is:

```yaml
---
swagger: "2.0"
info:
  version: "2020-02-01T23:52:35Z"
  title: "mailerex"
host: "j6ecw6w1j0.execute-api.ap-southeast-2.amazonaws.com"
basePath: "/messages"
schemes:
- "https"
paths:
  /:
    post:
      consumes:
      - "application/json"
      produces:
      - "application/json"
      parameters:
      - in: "body"
        name: "MailerRequest"
        required: true
        schema:
          $ref: "#/definitions/MailerRequest"
      responses:
        200:
          description: "200 response"
          schema:
            $ref: "#/definitions/MailerResponse"
definitions:
  MailerResponse:
    type: "object"
    required:
    - "mailer"
    - "message"
    properties:
      message:
        type: "string"
      mailer:
        type: "string"
    title: "Mailer Response"
  MailerRequest:
    type: "object"
    required:
    - "body"
    - "fromAddress"
    - "subject"
    - "toAddresses"
    properties:
      fromAddress:
        type: "string"
      toAddresses:
        type: "array"
        items:
          type: "string"
      ccAddresses:
        type: "array"
        items:
          type: "string"
      bccAddresses:
        type: "array"
        items:
          type: "string"
      subject:
        type: "string"
      body:
        type: "string"
    title: "Mailer Request"

```

The service can be invoked as follows:

```http request
POST /messages HTTP/1.1
Host: j6ecw6w1j0.execute-api.ap-southeast-2.amazonaws.com
Content-Type: application/json

{
    "fromAddress": "barrett.rob@gmail.com",
    "toAddresses": [ "barrett.rob@gmail.com" ],
    "ccAddresses": [ "barrett.rob@example.com" ],
    "bccAddresses": [ "barrett.rob@mailinator.com" ],
    "subject": "RESTful invocation",
    "body": "lorem ipsum..."
}
```

For successful requests the response will be one of:

```http request
HTTP/1.1 200 OK
Content-Type: application/json
...
{"message":"mail sent","mailer":"MailGunMailer"}
``` 
or
```http request
HTTP/1.1 200 OK
Content-Type: application/json
...
{"message":"mail sent","mailer":"SendGridMailer"}
``` 

For unsuccessful requests the response will be something like:

```http request
HTTP/1.1 400 Bad Request
Content-Type: text/plain
...

missing 'toAddress' addresses
```

----

## Notes:

Libraries used for this solution: (see the `dependencies` section 
in [build.gradle](/build.gradle))

- Apache HTTPClient
- Apache Commons Lang
- AWS Serverless Java Container Core
- Sun javax.mail library (for email address validation only)


For the purposes of this example, the mailer implementation is selected randomly, 
but in production the mailer would probably be selected based on various real-world 
factors, for example

- cost
- latency
- availability
- error rates
- throughput rates

I'm not sure from the problem description whether there should rules around the success 
or failure of a mailer that would determine that other mailers should be used on the next
call, or whether an attempt to send the mail via another provider should immediately be
made?

Additionally, we also need tests to check whether email was actually delivered in the real 
world. This requires a bit more setup so was not done for the purposes of the example
implementation. 

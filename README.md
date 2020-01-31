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
      library is used for HTTP requests.

#### TODO: 

Non-crucial features left unimplemented.

- handling of html/rich text/mime content
- attachments
- monitoring
- retry after network failure
- load tests

----

The implementation is done using the following technologies:

Code:

- java8
- [Apache HTTPClient](https://hc.apache.org/httpcomponents-client-ga/index.html)
- MailGun API as described here: https://documentation.mailgun.com/en/latest/api-sending.html#sending
- SendGrid API as described here: https://sendgrid.com/docs/API_Reference/Web_API_v3/index.html

Note: a production implementation should make use fo dependency injection, but for
the purposes of this example the implementations are explicitly created and injected.

Deployment:
 
- the solution is deployed as a [AWS Lambda](https://aws.amazon.com/lambda/) fronted by
  an API defined in the [Amazon API Gateway](https://aws.amazon.com/api-gateway/)
- the deployed service can be invoked as follows:

```http request
example http request

```

---- 

The solution can be compiled as a java application. The build tool of choice in this
implementation is [Gradle](https://gradle.org/) and the build can be invoked as follows:

`./gradlew clean build test jar`

This will build the solution and run all the unit tests.

Deployment of the lambda was done manually, as was the definition of the API in the
Amazon API Gateway. 

Automated deployment was not done as part of this implementation, but the 
[AWS Serverless Application Model](https://aws.amazon.com/serverless/sam/) 
would be an appropriate choice for deployment.

----

Libraries used for this solution: (see the `dependencies` section 
in [build.gradle](/build.gradle))

- Apache HTTPClient
- Apache Commons Lang
- AWS Serverless Java Container Core

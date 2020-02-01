package org.example.mailerex.services;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by robertb on 1/2/20.
 */
public class ConfigurationService {

    private final Properties configurationProperties;

    public ConfigurationService() {
        try {
            this.configurationProperties = new Properties();
            this.configurationProperties.load(
                    getClass().getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMailGunBaseUrl() {
        return configurationProperties.getProperty("mailgun.baseurl");
    }

    public String getMailGunApiKey() {
        return configurationProperties.getProperty("mailgun.apikey");
    }

    public String getSendGridBaseUrl() {
        return configurationProperties.getProperty("sendgrid.baseurl");
    }

    public String getSendGridApiKey() {
        return configurationProperties.getProperty("sendgrid.apikey");
    }
}

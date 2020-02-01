package org.example.mailerex.services;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * Created by robertb on 1/2/20.
 */
public class ConfigurationServiceTest {

    private ConfigurationService configurationService = new ConfigurationService();

    @Test
    public void test0() {
        assertNotNull(configurationService.getMailGunBaseUrl());
    }


}
package com.musichub.bootstrap.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

/**
 * Application configuration class for the Music Data Hub.
 * This class is responsible for configuring the application's dependency injection.
 */
@ApplicationScoped
public class ApplicationConfig {
    
    private static final Logger LOG = Logger.getLogger(ApplicationConfig.class);
    
    /**
     * Constructor for ApplicationConfig.
     * Logs that the application configuration is being initialized.
     */
    public ApplicationConfig() {
        LOG.info("Initializing Music Data Hub application configuration");
    }



}
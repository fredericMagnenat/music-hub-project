package com.musichub.bootstrap.startup;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.jboss.logging.Logger;

/**
 * Application startup class for the Music Data Hub.
 * This class is responsible for initializing the application on startup.
 */
@ApplicationScoped
public class ApplicationStartup {
    
    private static final Logger LOG = Logger.getLogger(ApplicationStartup.class);
    
    /**
     * Method that is called when the application starts up.
     * 
     * @param event The startup event
     */
    public void onStart(@Observes StartupEvent event) {
        LOG.info("Music Data Hub application is starting up");
        
        // Perform any necessary initialization here
        initializeApplication();
        
        LOG.info("Music Data Hub application initialization completed");
    }
    
    /**
     * Initializes the application.
     * This method can be extended to perform any necessary initialization tasks.
     */
    private void initializeApplication() {
        LOG.debug("Initializing application components");
        
        // Add initialization code here
        // For example:
        // - Initialize caches
        // - Preload data
        // - Start background tasks
        
        LOG.debug("Application components initialized");
    }
}
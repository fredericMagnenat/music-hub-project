package com.musichub.bootstrap;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

/**
 * Main application class for the Music Data Hub.
 * This class is responsible for starting the Quarkus application.
 */
@QuarkusMain
public class MusicHubApplication {

    private static final Logger LOG = Logger.getLogger(MusicHubApplication.class);

    public static void main(String... args) {
        LOG.info("Starting Music Data Hub application...");
        Quarkus.run(MusicHubApp.class, args);
    }


    public static class MusicHubApp implements QuarkusApplication {

        @Override
        public int run(String... args) {
            LOG.info("Music Data Hub application started successfully");
            Quarkus.waitForExit();
            return 0;
        }
    }
}
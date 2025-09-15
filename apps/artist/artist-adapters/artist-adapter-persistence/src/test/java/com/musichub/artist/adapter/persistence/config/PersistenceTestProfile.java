
package com.musichub.artist.adapter.persistence.config;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PersistenceTestProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
        Map<String, String> config = new HashMap<>();

        // Exclusion des composants de l'application
        config.put("quarkus.arc.exclude-types", "com.musichub.artist.application.**");
        return config;
    }

    @Override
    public Set<Class<?>> getEnabledAlternatives() {
        return Collections.emptySet();
    }

    @Override
    public boolean disableApplicationLifecycleObservers() {
        return true;
    }
}

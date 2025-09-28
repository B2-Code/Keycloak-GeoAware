package org.b2code.config;

import org.keycloak.testframework.realm.RealmConfig;
import org.keycloak.testframework.realm.RealmConfigBuilder;

public class TestRealmConfig implements RealmConfig {

    @Override
        public RealmConfigBuilder configure(RealmConfigBuilder realmConfigBuilder) {
        realmConfigBuilder.name("test-realm")
                .displayName("Test Realm");

        return realmConfigBuilder;
    }
}

package org.b2code.config;

import org.keycloak.testframework.realm.RealmConfig;
import org.keycloak.testframework.realm.RealmConfigBuilder;

public class RealmAConfig implements RealmConfig {

    @Override
    public RealmConfigBuilder configure(RealmConfigBuilder realmConfigBuilder) {
        realmConfigBuilder.addClient("test-client")
                .secret("test-secret")
                .directAccessGrants();

        realmConfigBuilder.addUser("test-user")
                .name("Test", "User")
                .email("test-user@test-domain.com")
                .password("test-password")
                .emailVerified();

        return realmConfigBuilder;
    }
}

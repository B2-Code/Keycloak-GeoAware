package org.b2code.config;

import org.keycloak.testframework.realm.UserConfig;
import org.keycloak.testframework.realm.UserConfigBuilder;

public class TestUserConfig implements UserConfig {

    @Override
    public UserConfigBuilder configure(UserConfigBuilder userConfigBuilder) {
        return userConfigBuilder.username("test-user")
                .name("Test", "User")
                .email("test-user@test-domain.com")
                .password("test-password")
                .emailVerified(true);
    }

}

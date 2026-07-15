package org.b2code.config;

import org.keycloak.testframework.realm.UserBuilder;
import org.keycloak.testframework.realm.UserConfig;

public class TestUserConfig implements UserConfig {

    @Override
    public UserBuilder configure(UserBuilder userBuilder) {
        return userBuilder.username("test-user")
                .name("Test", "User")
                .email("test-user@test-domain.com")
                .password("test-password")
                .emailVerified(true);
    }

}

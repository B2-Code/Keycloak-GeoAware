package org.b2code.config;

import org.keycloak.testframework.realm.ClientBuilder;
import org.keycloak.testframework.realm.ClientConfig;

public class TestClientConfig implements ClientConfig {

    @Override
    public ClientBuilder configure(ClientBuilder clientBuilder) {
        return clientBuilder
                .clientId("test-client")
                .secret("test-secret")
                .redirectUris("*")
                .protocol("openid-connect")
                .directAccessGrantsEnabled(true);
    }
}

package org.b2code.config;

import org.keycloak.testframework.realm.ClientConfig;
import org.keycloak.testframework.realm.ClientConfigBuilder;

public class TestClientConfig implements ClientConfig {

    @Override
    public ClientConfigBuilder configure(ClientConfigBuilder clientConfigBuilder) {
        return clientConfigBuilder
                .clientId("test-client")
                .secret("test-secret")
                .redirectUris("*")
                .protocol("openid-connect")
                .directAccessGrants();
    }
}

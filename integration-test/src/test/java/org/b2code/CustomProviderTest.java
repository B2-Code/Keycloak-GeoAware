package org.b2code;

import org.b2code.config.RealmAConfig;
import org.b2code.config.ServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.testframework.annotations.InjectRealm;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;
import org.keycloak.testframework.injection.LifeCycle;
import org.keycloak.testframework.realm.ManagedRealm;

@KeycloakIntegrationTest(config = ServerConfig.class)
class CustomProviderTest {

    @InjectRealm(lifecycle = LifeCycle.CLASS, config = RealmAConfig.class)
    private ManagedRealm realm;

    @Test
    void testCreatedClient() {
        Assertions.assertEquals(1, realm.getCreatedRepresentation().getClients().size());
        Assertions.assertEquals("test-client", realm.getCreatedRepresentation().getClients().getFirst().getClientId());
    }
}
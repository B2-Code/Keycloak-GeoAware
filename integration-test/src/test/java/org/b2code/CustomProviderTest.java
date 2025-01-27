package org.b2code;

import org.b2code.config.RealmAConfig;
import org.b2code.config.ServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.testframework.annotations.InjectAdminClient;
import org.keycloak.testframework.annotations.InjectRealm;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;
import org.keycloak.testframework.realm.ManagedRealm;

import java.util.List;

@KeycloakIntegrationTest(config = ServerConfig.class)
public class CustomProviderTest {

    @InjectRealm(config = RealmAConfig.class)
    private ManagedRealm realm;

    @InjectAdminClient
    Keycloak adminClient;

    @Test
    public void testCreateRealm() {
        Assertions.assertEquals("realmA", realm.getName());
    }
    
    @Test
    public void testCreatedClient() {
        RealmResource realmResource = adminClient.realm(realm.getName());
        List<ClientRepresentation> clients = realmResource.clients().findAll();
    }
}
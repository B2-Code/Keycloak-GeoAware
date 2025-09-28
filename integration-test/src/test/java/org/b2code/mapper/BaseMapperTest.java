package org.b2code.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import lombok.extern.slf4j.Slf4j;
import org.b2code.base.BaseTest;
import org.b2code.util.AdminEventPaths;
import org.keycloak.admin.client.resource.ClientScopesResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.testframework.annotations.InjectAdminEvents;
import org.keycloak.testframework.events.AdminEventAssertion;
import org.keycloak.testframework.events.AdminEvents;
import org.keycloak.testframework.util.ApiUtil;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
abstract class BaseMapperTest extends BaseTest {

    @InjectAdminEvents
    AdminEvents adminEvents;

    protected ProtocolMapperRepresentation makeMapper(String name, String mapperType, Map<String, String> config) {
        ProtocolMapperRepresentation rep = new ProtocolMapperRepresentation();
        rep.setProtocol("openid-connect");
        rep.setName(name);
        rep.setProtocolMapper(mapperType);
        rep.setConfig(config);
        return rep;
    }

    protected void assertEqualMappers(ProtocolMapperRepresentation original, ProtocolMapperRepresentation created) {
        assertNotNull(created);
        assertEquals(original.getName(), created.getName());
        assertEquals(original.getConfig(), created.getConfig());
        assertEquals(original.getProtocol(), created.getProtocol());
        assertEquals(original.getProtocolMapper(), created.getProtocolMapper());
    }
}

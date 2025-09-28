package org.b2code.util;

import jakarta.ws.rs.core.UriBuilder;
import org.keycloak.admin.client.resource.*;

import java.net.URI;

public class AdminEventPaths {

    // Client Resource

    public static String clientResourcePath(String clientDbId) {
        URI uri = UriBuilder.fromUri("").path(RealmResource.class, "clients").path(ClientsResource.class, "get").build(clientDbId);
        return uri.toString();
    }

    public static String clientProtocolMappersPath(String clientDbId) {
        URI uri = UriBuilder.fromUri(clientResourcePath(clientDbId))
                .path(ClientResource.class, "getProtocolMappers")
                .build();
        return uri.toString();
    }

    public static String clientProtocolMapperPath(String clientDbId, String protocolMapperId) {
        URI uri = UriBuilder.fromUri(clientProtocolMappersPath(clientDbId))
                .path(ProtocolMappersResource.class, "getMapperById")
                .build(protocolMapperId);
        return uri.toString();
    }
}

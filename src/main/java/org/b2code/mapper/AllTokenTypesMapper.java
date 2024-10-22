package org.b2code.mapper;

import org.keycloak.protocol.oidc.mappers.*;

public interface AllTokenTypesMapper extends OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper,
        OIDCAccessTokenResponseMapper, TokenIntrospectionTokenMapper {
}

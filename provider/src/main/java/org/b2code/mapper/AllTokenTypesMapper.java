package org.b2code.mapper;

import org.b2code.ServerInfo;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ServerInfoAwareProviderFactory;

import java.util.Map;

public abstract class AllTokenTypesMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper,
        OIDCAccessTokenResponseMapper, TokenIntrospectionTokenMapper, ServerInfoAwareProviderFactory {

    @Override
    public Map<String, String> getOperationalInfo() {
        return ServerInfo.INFO_MAP;
    }

}

package org.b2code;

import org.keycloak.provider.ServerInfoAwareProviderFactory;

import java.util.Map;

public abstract class ServerInfoAwareFactory implements ServerInfoAwareProviderFactory {

    @Override
    public Map<String, String> getOperationalInfo() {
        return ServerInfo.INFO_MAP;
    }

}

package org.b2code;

import org.keycloak.provider.ServerInfoAwareProviderFactory;

import java.util.Map;
import java.util.Objects;

public abstract class ServerInfoAwareFactory implements ServerInfoAwareProviderFactory {

    private static final Map<String, String> INFO_MAP = Map.of(
            "Version", Objects.requireNonNullElse(ServerInfoAwareFactory.class.getPackage().getImplementationVersion(), "dev"),
            "Plugin", PluginConstants.PLUGIN_NAME
    );

    @Override
    public Map<String, String> getOperationalInfo() {
        return INFO_MAP;
    }

}

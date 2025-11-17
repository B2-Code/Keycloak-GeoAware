package org.b2code;

import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Objects;

@UtilityClass
public class ServerInfo {

    public static final Map<String, String> INFO_MAP = Map.of(
            "Version", Objects.requireNonNullElse(ServerInfo.class.getPackage().getImplementationVersion(), "dev"),
            "Plugin", PluginConstants.PLUGIN_NAME
    );

}

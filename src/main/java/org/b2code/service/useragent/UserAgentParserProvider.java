package org.b2code.service.useragent;

import org.keycloak.provider.Provider;

public interface UserAgentParserProvider extends Provider {

    UserAgentInfo parse(String userAgent);
}

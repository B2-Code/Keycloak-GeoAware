package org.b2code.service.loginhistory;

import org.keycloak.provider.Provider;

public interface LoginHistoryProvider extends Provider {

    void track();

    boolean isKnownIp();

    boolean isKnownDevice();

}

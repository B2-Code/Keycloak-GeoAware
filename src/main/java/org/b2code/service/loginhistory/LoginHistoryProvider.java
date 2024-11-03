package org.b2code.service.loginhistory;

import org.keycloak.provider.Provider;

import java.util.List;

public interface LoginHistoryProvider extends Provider {

    /**
     * @return true if the ip is already in the history, false otherwise
     */
    boolean isKnownIp();

    /**
     * @return true if the device is already in the history, false otherwise
     */
    boolean isKnownDevice();

    /**
     * @return the history of logins as immutable list
     */
    List<LoginRecord> getHistory();

    /**
     * Tracks the current login
     * Is automatically called by the {@link LoginTrackerEventListenerProvider}
     */
    void track();

}

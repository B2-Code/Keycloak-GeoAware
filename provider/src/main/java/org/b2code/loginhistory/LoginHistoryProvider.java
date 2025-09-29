package org.b2code.loginhistory;

import org.keycloak.provider.Provider;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
     * The location is determined by the IP address.
     * As this is quite imprecise, an accuracy radius is used to determine whether two locations are considered the same.
     *
     * @return true if the location is already in the history, false otherwise
     */
    boolean isUnknownLocation();

    /**
     * @return the history of logins as stream
     */
    Stream<LoginRecord> getHistoryStream();

    /**
     * @return the history of logins as list
     */
    List<LoginRecord> getHistory();

    /**
     * Tracks the current login
     * Is automatically called by the {@link LoginTrackerEventListenerProvider}
     */
    void track();

    /**
     * @return the last login record
     */
    Optional<LoginRecord> getLastLogin();
}

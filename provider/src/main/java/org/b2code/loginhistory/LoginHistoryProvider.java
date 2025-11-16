package org.b2code.loginhistory;

import org.b2code.geoip.persistence.entity.LoginRecordEntity;
import org.keycloak.events.Event;
import org.keycloak.provider.Provider;
import org.keycloak.representations.account.DeviceRepresentation;

import java.util.Optional;

public interface LoginHistoryProvider extends Provider {

    /**
     * @return true if the ip is already in the history, false otherwise
     */
    boolean isKnownIp();

    /**
     * @return true if the device is not already in the history, false otherwise
     */
    boolean isUnknownDevice();

    /**
     * The location is determined by the IP address.
     * As this is quite imprecise, an accuracy radius is used to determine whether two locations are considered the same.
     *
     * @return true if the location is unknown, false otherwise
     */
    boolean isUnknownLocation();

    /**
     * Tracks the current login
     * Is automatically called by the {@link LoginTrackerEventListenerProvider}
     */
    void track(DeviceRepresentation device, Event event);

    /**
     * @return the last login record
     */
    Optional<LoginRecordEntity> getLastLogin();
}

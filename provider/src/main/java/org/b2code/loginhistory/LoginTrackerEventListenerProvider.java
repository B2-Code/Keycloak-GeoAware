package org.b2code.loginhistory;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.device.DeviceRepresentationProvider;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerTransaction;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.account.DeviceRepresentation;

@JBossLog
public class LoginTrackerEventListenerProvider implements EventListenerProvider {

    private final KeycloakSession session;

    private final EventListenerTransaction tx = new EventListenerTransaction(null, this::trackLogin);

    public LoginTrackerEventListenerProvider(KeycloakSession session) {
        this.session = session;
        session.getTransactionManager().enlistAfterCompletion(tx);
    }

    @Override
    public void onEvent(Event event) {
        if (EventType.LOGIN == event.getType()) {
            tx.addEvent(event);
        }
    }

    private void trackLogin(Event event) {
        log.debug("Tracking login event");
        try {
            DeviceRepresentationProvider deviceRepProvider = session.getProvider(DeviceRepresentationProvider.class);
            DeviceRepresentation deviceRep = deviceRepProvider.deviceRepresentation();
            session.getProvider(LoginHistoryProvider.class).track(deviceRep, event);
        } catch (Exception e) {
            log.error("Failed to track login event", e);
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }
}

package org.b2code.service.loginhistory;

import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

@JBossLog
@RequiredArgsConstructor
public class LoginTrackerEventListenerProvider implements EventListenerProvider {

    private final KeycloakSession session;

    @Override
    public void onEvent(Event event) {
        if (EventType.LOGIN == event.getType()) {
            log.debug("Tracking login event");
            session.getProvider(LoginHistoryProvider.class).track();
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

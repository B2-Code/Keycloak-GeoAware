package org.b2code.service.loginhistory;

import lombok.RequiredArgsConstructor;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

@RequiredArgsConstructor
public class LoginTrackerEventListenerProvider implements EventListenerProvider {

    private final KeycloakSession session;

    @Override
    public void onEvent(Event event) {
        if (event.getType() == EventType.LOGIN) {
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

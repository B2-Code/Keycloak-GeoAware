package org.b2code.loginhistory;

import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.admin.PluginConfigWrapper;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

@JBossLog
@RequiredArgsConstructor
public class LoginTrackerEventListenerProvider implements EventListenerProvider {

    private final KeycloakSession session;

    @Override
    public void onEvent(Event event) {
        if (EventType.LOGIN == event.getType()) {
            RealmModel realm = session.realms().getRealm(event.getRealmId());
            if (new PluginConfigWrapper(realm).isPluginEnabled()) {
                log.debug("Tracking login event");
                session.getProvider(LoginHistoryProvider.class).track();
            } else {
                log.debug("Login tracking is disabled for realm " + event.getRealmName());
            }
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

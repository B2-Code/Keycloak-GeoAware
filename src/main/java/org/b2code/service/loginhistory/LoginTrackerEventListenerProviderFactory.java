package org.b2code.service.loginhistory;

import com.google.auto.service.AutoService;
import org.b2code.PluginConstants;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.models.utils.PostMigrationEvent;

import java.util.Set;
import java.util.stream.Collectors;

@AutoService(EventListenerProviderFactory.class)
public class LoginTrackerEventListenerProviderFactory implements EventListenerProviderFactory {

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new LoginTrackerEventListenerProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
        // NOOP
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        factory.register(event -> {
            if (event instanceof RealmModel.RealmPostCreateEvent postCreateEvent) {
                this.activateEventListenerInRealm(postCreateEvent.getCreatedRealm());
            } else if (event instanceof PostMigrationEvent) {
                KeycloakModelUtils.runJobInTransaction(factory, this::activateEventListenerInAllRealms);
            }
        });
    }

    private void activateEventListenerInRealm(RealmModel realm) {
        Set<String> evenListeners = realm.getEventsListenersStream().collect(Collectors.toSet());
        if (!evenListeners.contains(getId())) {
            evenListeners.add(getId());
            realm.setEventsListeners(evenListeners);
        }
    }

    private void activateEventListenerInAllRealms(KeycloakSession session) {
        RealmProvider realmProvider = session.getProvider(RealmProvider.class);
        realmProvider.getRealmsStream().forEach(this::activateEventListenerInRealm);
    }

    @Override
    public void close() {
        // NOOP
    }

    @Override
    public String getId() {
        return PluginConstants.PLUGIN_NAME_LOWER_CASE + "-login-tracker";
    }
}

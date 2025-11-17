package org.b2code.loginhistory;

import com.google.auto.service.AutoService;
import org.b2code.PluginConstants;
import org.b2code.ServerInfoAwareFactory;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(EventListenerProviderFactory.class)
public class LoginTrackerEventListenerProviderFactory extends ServerInfoAwareFactory implements EventListenerProviderFactory {

    public static final String ID = PluginConstants.PLUGIN_NAME_LOWER_CASE + "-login-tracker";

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
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }

    @Override
    public String getId() {
        return ID;
    }
}

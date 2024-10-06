package org.b2code.service.iphistory;

import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@JBossLog
@AutoService(IpHistoryProviderFactory.class)
public class DefaultIpHistoryProviderFactory implements IpHistoryProviderFactory {

    private static final String RECORD_RETENTION_TIME_HOURS_PARAM = "retentionTimeHours";
    private static final String RECORD_RETENTION_TIME_HOURS_DEFAULT = "24";
    private Config.Scope config;

    @Override
    public DefaultIpHistoryProvider create(KeycloakSession session) {
        int retentionTimeHours = Integer.parseInt(config.get(RECORD_RETENTION_TIME_HOURS_PARAM, RECORD_RETENTION_TIME_HOURS_DEFAULT));
        return new DefaultIpHistoryProvider(session, retentionTimeHours);
    }

    @Override
    public void init(Config.Scope scope) {
        this.config = scope;
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "default";
    }
}

package org.b2code.geoip.maxmind;

import com.google.auto.service.AutoService;
import com.maxmind.geoip2.DatabaseReader;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.ServerInfoAwareFactory;
import org.b2code.geoip.GeoIpProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.timer.TimerProvider;
import org.keycloak.utils.KeycloakSessionUtil;

import java.io.IOException;
import java.time.Duration;

@JBossLog
@AutoService(GeoIpProviderFactory.class)
public class MaxmindFileAutodownloadProviderFactory extends ServerInfoAwareFactory implements GeoIpProviderFactory {

    public static final String PROVIDER_ID = "maxmind-file-autodownload";
    public static final int REFRESH_INTERVAL_HOURS = 1;

    private static final UpdateMaxmindDatabaseFileTask TASK_INSTANCE = new UpdateMaxmindDatabaseFileTask();

    @Getter
    @Setter
    private DatabaseReader reader;

    @Override
    public MaxmindFileAutodownloadProvider create(KeycloakSession keycloakSession) {
        log.tracef("Creating new %s", MaxmindFileAutodownloadProvider.class.getSimpleName());
        if (reader == null) {
            TASK_INSTANCE.run(keycloakSession);
        }
        return new MaxmindFileAutodownloadProvider(keycloakSession, reader);
    }

    @Override
    public void init(Config.Scope scope) {
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        KeycloakSession keycloakSession = keycloakSessionFactory.create();
        TimerProvider timer = keycloakSession.getProvider(TimerProvider.class);
        timer.scheduleTask(TASK_INSTANCE, Duration.ofHours(REFRESH_INTERVAL_HOURS).toMillis());
        log.infof("Scheduled Maxmind database update task to run every %d hours", REFRESH_INTERVAL_HOURS);
    }

    @Override
    public void close() {
        try {
            if (this.reader != null) {
                this.reader.close();
            }
        } catch (IOException e) {
            log.warn("Failed to close Maxmind database reader", e);
        }
        TimerProvider timer = KeycloakSessionUtil.getKeycloakSession().getProvider(TimerProvider.class);
        timer.cancelTask(TASK_INSTANCE.getTaskName());
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}

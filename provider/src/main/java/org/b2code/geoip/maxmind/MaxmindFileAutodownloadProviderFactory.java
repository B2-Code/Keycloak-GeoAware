package org.b2code.geoip.maxmind;

import com.google.auto.service.AutoService;
import com.maxmind.geoip2.DatabaseReader;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.GeoIpProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.timer.TimerProvider;
import org.keycloak.utils.KeycloakSessionUtil;

import java.time.Duration;

@JBossLog
@AutoService(GeoIpProviderFactory.class)
public class MaxmindFileAutodownloadProviderFactory extends MaxmindProviderFactory {

    public static final String PROVIDER_ID = "maxmind-file-autodownload";

    private static final String DB_UPDATE_INTERVAL_HOURS_CONFIG_PARM = "updateIntervalHours";
    public static final int DB_UPDATE_INTERVAL_HOURS_DEFAULT = 24;

    private static final String MAXMIND_DB_DOWNLOAD_URL_CONFIG_PARAM = "downloadUrl";
    private static final String MAXMIND_DB_DOWNLOAD_URL_DEFAULT = "https://download.maxmind.com/geoip/databases/GeoLite2-City/download?suffix=tar.gz";


    private final UpdateMaxmindDatabaseFileTask TASK_INSTANCE = new UpdateMaxmindDatabaseFileTask(this);

    @Override
    public MaxmindProvider create(KeycloakSession keycloakSession) {
        log.tracef("Creating new %s", MaxmindProvider.class.getSimpleName());
        return new MaxmindProvider(keycloakSession, reader);
    }

    public int getUpdateIntervalHours() {
        return config.getInt(DB_UPDATE_INTERVAL_HOURS_CONFIG_PARM, DB_UPDATE_INTERVAL_HOURS_DEFAULT);
    }

    public String getMaxmindDbDownloadUrl() {
        return config.get(MAXMIND_DB_DOWNLOAD_URL_CONFIG_PARAM, MAXMIND_DB_DOWNLOAD_URL_DEFAULT);
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        KeycloakSession keycloakSession = keycloakSessionFactory.create();
        TimerProvider timer = keycloakSession.getProvider(TimerProvider.class);
        timer.scheduleTask(TASK_INSTANCE, Duration.ofHours(getUpdateIntervalHours()).toMillis());
        log.infof("Scheduled Maxmind database update task to run every %d hours", getUpdateIntervalHours());
        TASK_INSTANCE.run(keycloakSession);
    }

    @Override
    public DatabaseReader createReader() {
        return TASK_INSTANCE.getReader();
    }

    @Override
    public void close() {
        super.close();
        TimerProvider timer = KeycloakSessionUtil.getKeycloakSession().getProvider(TimerProvider.class);
        timer.cancelTask(TASK_INSTANCE.getTaskName());
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}

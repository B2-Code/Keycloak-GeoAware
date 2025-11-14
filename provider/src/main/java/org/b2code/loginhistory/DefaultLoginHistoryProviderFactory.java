package org.b2code.loginhistory;

import com.google.auto.service.AutoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.persistence.repository.LoginRecordRepository;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.provider.Provider;
import org.keycloak.timer.TimerProvider;

import java.time.Duration;
import java.util.Set;

@RequiredArgsConstructor
@JBossLog
@AutoService(LoginHistoryProviderFactory.class)
public class DefaultLoginHistoryProviderFactory implements LoginHistoryProviderFactory {

    private static final String LOGIN_HISTORY_RETENTION_HOURS_CONFIG_PARM = "retentionHours";
    private static final int LOGIN_HISTORY_RETENTION_HOURS_DEFAULT = 24 * 7;

    private static final String LOGIN_HISTORY_CLEANUP_INTERVAL_MINUTES_CONFIG_PARM = "cleanupIntervalMinutes";
    private static final int LOGIN_HISTORY_CLEANUP_INTERVAL_MINUTES_DEFAULT = 60;

    private Config.Scope config;

    @Override
    public DefaultLoginHistoryProvider create(KeycloakSession session) {
        return new DefaultLoginHistoryProvider(session);
    }

    @Override
    public void init(Config.Scope scope) {
        this.config = scope;
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        keycloakSessionFactory.register(event -> {
            if (event instanceof UserModel.UserRemovedEvent userRemovedEvent) {
                KeycloakModelUtils.runJobInTransaction(keycloakSessionFactory, session -> handleUserRemoved(session, userRemovedEvent));
            } else if (event instanceof RealmModel.RealmRemovedEvent realmRemovedEvent) {
                KeycloakModelUtils.runJobInTransaction(keycloakSessionFactory, session -> handleRealmRemoved(session, realmRemovedEvent));
            }
        });

        KeycloakSession session = keycloakSessionFactory.create();
        initCleanUpTimer(session);
    }

    private void handleUserRemoved(KeycloakSession session, UserModel.UserRemovedEvent event) {
        LoginRecordRepository repo = session.getProvider(LoginRecordRepository.class);
        repo.deleteByUserId(event.getUser().getId());
    }

    private void handleRealmRemoved(KeycloakSession session, RealmModel.RealmRemovedEvent event) {
        LoginRecordRepository repo = session.getProvider(LoginRecordRepository.class);
        repo.deleteByRealmId(event.getRealm().getId());
    }

    private int getLoginHistoryRetentionHours() {
        return config.getInt(LOGIN_HISTORY_RETENTION_HOURS_CONFIG_PARM, LOGIN_HISTORY_RETENTION_HOURS_DEFAULT);
    }

    private int getLoginHistoryCleanupIntervalMinutes() {
        return config.getInt(LOGIN_HISTORY_CLEANUP_INTERVAL_MINUTES_CONFIG_PARM, LOGIN_HISTORY_CLEANUP_INTERVAL_MINUTES_DEFAULT);
    }

    private void initCleanUpTimer(KeycloakSession session) {
        int retentionHours = getLoginHistoryRetentionHours();
        if (retentionHours <= 0) {
            log.infof("Login history retention hours is set to %d hours. Cleanup timer will not be initialized.", retentionHours);
            return;
        }
        int cleanupIntervalMinutes = getLoginHistoryCleanupIntervalMinutes();
        if (cleanupIntervalMinutes <= 0) {
            log.infof("Login history cleanup interval is set to %d minutes. Cleanup timer will not be initialized.", cleanupIntervalMinutes);
            return;
        }
        long cleanupIntervalMillis = Duration.ofMinutes(cleanupIntervalMinutes).toMillis();
        TimerProvider timer = session.getProvider(TimerProvider.class);
        LoginHistoryCleanupTask cleanupTask = new LoginHistoryCleanupTask(retentionHours);
        timer.cancelTask(cleanupTask.getTaskName());
        log.infof("Initializing login history cleanup timer with retention period of %d hours and cleanup interval of %d minutes", retentionHours, cleanupIntervalMinutes);
        timer.scheduleTask(cleanupTask, cleanupIntervalMillis);
    }

    @Override
    public Set<Class<? extends Provider>> dependsOn() {
        return Set.of(LoginRecordRepository.class);
    }

    @Override
    public void close() {
        // NOOP
    }

    @Override
    public String getId() {
        return "default";
    }
}

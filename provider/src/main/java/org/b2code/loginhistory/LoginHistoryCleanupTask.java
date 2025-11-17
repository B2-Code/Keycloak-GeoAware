package org.b2code.loginhistory;

import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.PluginConstants;
import org.b2code.geoip.persistence.repository.LoginRecordRepository;
import org.keycloak.models.KeycloakSession;
import org.keycloak.timer.ScheduledTask;

@JBossLog
@RequiredArgsConstructor
public class LoginHistoryCleanupTask implements ScheduledTask {

    private final int retentionHours;

    @Override
    public void run(KeycloakSession session) {
        LoginRecordRepository loginRecordRepository = session.getProvider(LoginRecordRepository.class);
        log.debugf("Starting login history cleanup task, removing records older than %d hours", retentionHours);
        long numDeletedRecords = loginRecordRepository.cleanupOldRecords(retentionHours);
        log.debugf("Login history cleanup task completed, removed %d old record(s)", numDeletedRecords);
    }

    @Override
    public String getTaskName() {
        return PluginConstants.PLUGIN_NAME + "_" + getClass().getSimpleName();
    }

}

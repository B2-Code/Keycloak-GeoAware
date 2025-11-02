package org.b2code.loginhistory;

import lombok.RequiredArgsConstructor;
import org.b2code.geoip.persistence.repository.LoginRecordRepository;
import org.keycloak.models.KeycloakSession;
import org.keycloak.timer.ScheduledTask;

@RequiredArgsConstructor
public class LoginHistoryCleanupTask implements ScheduledTask {

    private final int retentionHours;

    @Override
    public void run(KeycloakSession session) {
        LoginRecordRepository loginRecordRepository = session.getProvider(LoginRecordRepository.class);
        loginRecordRepository.cleanupOldRecords(retentionHours);
    }

    @Override
    public String getTaskName() {
        return getClass().getSimpleName();
    }


}

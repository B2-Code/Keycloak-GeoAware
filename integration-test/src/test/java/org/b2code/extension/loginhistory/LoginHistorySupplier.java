package org.b2code.extension.loginhistory;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.testframework.database.TestDatabase;
import org.keycloak.testframework.injection.InstanceContext;
import org.keycloak.testframework.injection.LifeCycle;
import org.keycloak.testframework.injection.RequestedInstance;
import org.keycloak.testframework.injection.Supplier;

import java.util.Map;

@Slf4j
public class LoginHistorySupplier implements Supplier<LoginHistory, InjectLoginHistory> {

    @Override
    public LoginHistory getValue(InstanceContext<LoginHistory, InjectLoginHistory> instanceContext) {
        Map<String, String> config = instanceContext.getDependency(TestDatabase.class).serverConfig();
        log.debug("Using database: {}", config.get("db-url"));
        log.debug("Using username: {}", config.get("db-username"));
        log.debug("Using password: {}", config.get("db-password"));

        return new LoginHistory(config.get("db-url"), config.get("db-username"), config.get("db-password"));
    }

    @Override
    public LifeCycle getDefaultLifecycle() {
        return LifeCycle.CLASS;
    }

    @Override
    public boolean compatible(InstanceContext<LoginHistory, InjectLoginHistory> a, RequestedInstance<LoginHistory, InjectLoginHistory> b) {
        return false;
    }

}

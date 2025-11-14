package org.b2code.extension.loginhistory;

import com.google.auto.service.AutoService;
import org.keycloak.testframework.TestFrameworkExtension;
import org.keycloak.testframework.database.TestDatabase;
import org.keycloak.testframework.injection.Supplier;

import java.util.List;

@AutoService(TestFrameworkExtension.class)
public class LoginHistoryTestFrameworkExtension implements TestFrameworkExtension {

    @Override
    public List<Supplier<?, ?>> suppliers() {
        return List.of(new LoginHistorySupplier());
    }

    @Override
    public List<Class<?>> alwaysEnabledValueTypes() {
        return List.of(LoginHistory.class, TestDatabase.class);
    }
}

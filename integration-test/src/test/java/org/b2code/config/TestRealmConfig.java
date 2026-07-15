package org.b2code.config;

import org.b2code.authentication.device.DeviceAuthenticatorFactory;
import org.b2code.authentication.device.OnDeviceChangeConditionalAuthenticatorFactory;
import org.b2code.authentication.device.UnknownDeviceConditionalAuthenticatorFactory;
import org.b2code.authentication.ip.IpAuthenticatorFactory;
import org.b2code.authentication.ip.OnIpChangeConditionalAuthenticatorFactory;
import org.b2code.authentication.ip.UnknownIpConditionalAuthenticatorFactory;
import org.b2code.authentication.ip.UnknownLocationConditionalAuthenticatorFactory;
import org.b2code.loginhistory.LoginTrackerEventListenerProviderFactory;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordFormFactory;
import org.keycloak.testframework.realm.AuthenticationExecutionExportBuilder;
import org.keycloak.testframework.realm.AuthenticationFlowBuilder;
import org.keycloak.testframework.realm.RealmBuilder;
import org.keycloak.testframework.realm.RealmConfig;

import java.util.ArrayList;
import java.util.List;

public class TestRealmConfig implements RealmConfig {

    public static final String AUTHENTICATOR_FLOW_PREFIX = "test-flow-";
    public static final String CONDITIONAL_FLOW_PREFIX = "test-conditional-flow-";
    public static final String CONDITIONAL_SUB_FLOW_PREFIX = "sub-flow-";

    private static final List<String> AUTHENTICATOR_PROVIDERS = List.of(
            IpAuthenticatorFactory.PROVIDER_ID,
            DeviceAuthenticatorFactory.PROVIDER_ID
    );

    private static final List<String> CONDITIONAL_PROVIDERS = List.of(
            OnIpChangeConditionalAuthenticatorFactory.PROVIDER_ID,
            UnknownIpConditionalAuthenticatorFactory.PROVIDER_ID,
            UnknownLocationConditionalAuthenticatorFactory.PROVIDER_ID,
            OnDeviceChangeConditionalAuthenticatorFactory.PROVIDER_ID,
            UnknownDeviceConditionalAuthenticatorFactory.PROVIDER_ID
    );

    @Override
    public RealmBuilder configure(RealmBuilder builder) {
        builder.name("test-realm")
                .eventsListeners(LoginTrackerEventListenerProviderFactory.ID)
                .displayName("Test Realm");

        List<AuthenticationFlowBuilder> flows = new ArrayList<>();

        for (String provider : AUTHENTICATOR_PROVIDERS) {
            flows.add(AuthenticationFlowBuilder.create(
                            AUTHENTICATOR_FLOW_PREFIX + provider, "", "basic-flow", true, false)
                    .authenticationExecutions(
                            AuthenticationExecutionExportBuilder.authenticator(
                                    UsernamePasswordFormFactory.PROVIDER_ID, "REQUIRED", 10, false),
                            AuthenticationExecutionExportBuilder.authenticator(
                                    provider, "REQUIRED", 20, false)
                    ));
        }

        for (String provider : CONDITIONAL_PROVIDERS) {
            String subAlias = CONDITIONAL_SUB_FLOW_PREFIX + provider;

            flows.add(AuthenticationFlowBuilder.create(subAlias, "", "basic-flow", false, false)
                    .authenticationExecutions(
                            AuthenticationExecutionExportBuilder.authenticator(provider, "CONDITIONAL", 10, false),
                            AuthenticationExecutionExportBuilder.authenticator("deny-access-authenticator", "REQUIRED", 20, false)
                    ));

            flows.add(AuthenticationFlowBuilder.create(
                            CONDITIONAL_FLOW_PREFIX + provider, "", "basic-flow", true, false)
                    .authenticationExecutions(
                            AuthenticationExecutionExportBuilder.authenticator(
                                    UsernamePasswordFormFactory.PROVIDER_ID, "REQUIRED", 10, false),
                            AuthenticationExecutionExportBuilder.alias(subAlias, "CONDITIONAL", 20, false)
                    ));
        }

        builder.authenticationFlows(flows.toArray(new AuthenticationFlowBuilder[0]));

        return builder;
    }
}

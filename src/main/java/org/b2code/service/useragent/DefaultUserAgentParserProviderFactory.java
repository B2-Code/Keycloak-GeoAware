package org.b2code.service.useragent;

import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;
import com.google.auto.service.AutoService;
import com.google.common.base.Stopwatch;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.io.IOException;

@JBossLog
@AutoService(UserAgentParserProviderFactory.class)
public class DefaultUserAgentParserProviderFactory implements UserAgentParserProviderFactory {

    private UserAgentParser userAgentParser;

    @Override
    public DefaultUserAgentParserProvider create(KeycloakSession keycloakSession) {
        return new DefaultUserAgentParserProvider(getParser());
    }

    private UserAgentParser getParser() {
        if (userAgentParser == null) {
            try {
                Stopwatch stopwatch = Stopwatch.createStarted();
                userAgentParser = new UserAgentService().loadParser();
                log.debugf("UserAgentParser loaded in %s", stopwatch.stop());
            } catch (IOException e) {
                log.error("Failed to load UserAgentParser due to an I/O error", e);
            } catch (ParseException e) {
                log.error("Failed to load UserAgentParser due to an parsing error", e);
            }
        }
        return userAgentParser;
    }

    @Override
    public void init(Config.Scope scope) {
        // NOOP
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        if (userAgentParser == null) {
            userAgentParser = getParser();
        }
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

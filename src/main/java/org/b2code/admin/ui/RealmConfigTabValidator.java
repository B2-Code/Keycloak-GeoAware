package org.b2code.admin.ui;

import lombok.experimental.UtilityClass;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.PluginConstants;
import org.b2code.admin.PluginConfigOptions;
import org.b2code.admin.PluginConfigWrapper;
import org.b2code.geoip.maxmind.MaxmindFileProviderFactory;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ConfigurationValidationHelper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Optional;

@JBossLog
@UtilityClass
public class RealmConfigTabValidator {

    public static void validateConfiguration(RealmModel realm, ComponentModel model) throws ComponentValidationException {
        PluginConfigWrapper pluginConfig = new PluginConfigWrapper(realm);
        ConfigurationValidationHelper helper = ConfigurationValidationHelper.check(model);

        try {
            // Since it is not possible to create read-only fields with the declarative UI, we need to check if the configuration is managed by the environment and disallow changes if it is
            for (String key : model.getConfig().keySet()) {
                Optional<String> envValue = pluginConfig.getFromEnvConfig(key);
                if (envValue.isPresent() && !envValue.get().equals(model.getConfig().getFirst(key))) {
                    throw new ComponentValidationException("Configuration property ''{0}'' is managed by environment", key);
                }
            }

            helper.checkBoolean(PluginConfigOptions.ENABLED, PluginConfigOptions.ENABLED.isRequired());
            if (model.get(PluginConfigOptions.ENABLED.getName()).equals("false")) {
                return;
            }

            helper.checkInt(PluginConfigOptions.GEOIP_CACHE_SIZE, PluginConfigOptions.GEOIP_CACHE_SIZE.isRequired());
            helper.checkInt(PluginConfigOptions.LOGIN_HISTORY_RETENTION_DAYS, PluginConfigOptions.LOGIN_HISTORY_RETENTION_DAYS.isRequired());
            helper.checkInt(PluginConfigOptions.LOGIN_HISTORY_MAX_RECORDS, PluginConfigOptions.LOGIN_HISTORY_MAX_RECORDS.isRequired());

            helper.checkList(PluginConfigOptions.GEOIP_PROVIDER, PluginConfigOptions.GEOIP_PROVIDER.isRequired());
            if (model.get(PluginConfigOptions.GEOIP_PROVIDER.getName()).equals(MaxmindFileProviderFactory.PROVIDER_ID)) {
                helper.checkRequired(PluginConfigOptions.MAXMIND_FILE_PATH);
                String path = model.get(PluginConfigOptions.MAXMIND_FILE_PATH.getName());
                if (!path.endsWith(".mmdb")) {
                    throw new ComponentValidationException("''{0}'' must end with .mmdb", PluginConfigOptions.MAXMIND_FILE_PATH.getLabel());
                }
                Path p = Paths.get(path);
                if (!Files.exists(p)) {
                    throw new ComponentValidationException("No such file ''{0}''", path);
                }
            }
        } catch (ComponentValidationException e) {
            String message = MessageFormat.format(e.getMessage(), e.getParameters());
            log.errorf("Invalid %s configuration: %s", PluginConstants.PLUGIN_NAME, message);
            throw e;
        } catch (Exception e) {
            log.errorf("Invalid %s configuration: %s", PluginConstants.PLUGIN_NAME, e.getMessage());
            throw new ComponentValidationException(e.getMessage());
        }
    }
}

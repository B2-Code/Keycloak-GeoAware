package org.b2code.admin;

import lombok.extern.jbosslog.JBossLog;
import org.b2code.PluginConstants;
import org.b2code.admin.ui.RealmConfigTab;
import org.b2code.geoip.maxmind.MaxmindDatabase;
import org.keycloak.Config;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.utils.StringUtil;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

@JBossLog
public class PluginConfigWrapper {

    private static final Pattern SCOPE_PATH_PATTERN = Pattern.compile("_");

    private final RealmModel realm;

    public PluginConfigWrapper(RealmModel realm) {
        this.realm = realm;
    }

    public String get(ProviderConfigProperty property) {
        return get(property.getName(), (String) property.getDefaultValue());
    }

    public boolean isPluginEnabled() {
        return getBoolean(PluginConfigOptions.ENABLED);
    }

    public String getGeoipDatabaseProvider() {
        return get(PluginConfigOptions.GEOIP_PROVIDER);
    }

    public String getMaxmindDatabaseFilePath() {
        return get(PluginConfigOptions.MAXMIND_FILE_PATH);
    }

    public int getMaxmindAccountId() {
        return getInt(PluginConfigOptions.MAXMIND_ACCOUNT_ID);
    }

    public String getMaxmindLicenseKey() {
        return get(PluginConfigOptions.MAXMIND_LICENSE_KEY);
    }

    public MaxmindDatabase getMaxmindWebDatabase() {
        return MaxmindDatabase.fromLabel(get(PluginConfigOptions.MAXMIND_WEB_DATABASE));
    }

    public String getIpInfoToken() {
        return get(PluginConfigOptions.IPINFO_TOKEN);
    }

    public int getGeoipDatabaseCacheSize() {
        return getInt(PluginConfigOptions.GEOIP_CACHE_SIZE);
    }

    public int getGeoipDatabaseCacheHours() {
        return getInt(PluginConfigOptions.GEOIP_CACHE_HOURS);
    }

    public int getLoginHistoryRetentionDays() {
        return getInt(PluginConfigOptions.LOGIN_HISTORY_RETENTION_DAYS);
    }

    public int getLoginHistoryMaxRecords() {
        return getInt(PluginConfigOptions.LOGIN_HISTORY_MAX_RECORDS);
    }

    private boolean getBoolean(ProviderConfigProperty property) {
        return getBoolean(property.getName(), (String) property.getDefaultValue());
    }

    private boolean getBoolean(String key, String defaultValue) {
        return Boolean.parseBoolean(get(key, defaultValue));
    }

    private int getInt(ProviderConfigProperty property) {
        return getInt(property.getName(), (String) property.getDefaultValue());
    }

    private int getInt(String key, String defaultValue) {
        return Integer.parseInt(get(key, defaultValue));
    }

    private Config.Scope getRealmConfig() {
        return Config.scope(PluginConstants.PLUGIN_NAME_LOWER_CASE, "realm", realm.getName());
    }

    private Config.Scope getGlobalConfig() {
        return Config.scope(PluginConstants.PLUGIN_NAME_LOWER_CASE, "global");
    }

    private Optional<MultivaluedHashMap<String, String>> getAdminUiConfig() {
        return realm.getComponentsStream()
                .filter(component -> RealmConfigTab.PROVIDER_ID.equals(component.getProviderId()))
                .map(ComponentModel::getConfig)
                .findFirst();
    }

    public Optional<String> getFromEnvConfig(String key) {
        String[] keyParts = SCOPE_PATH_PATTERN.split(key);
        String scopeKey = keyParts[keyParts.length - 1];
        String[] scopeParts = Arrays.copyOf(keyParts, keyParts.length - 1);

        String envRealmValue = getRealmConfig().scope(scopeParts).get(scopeKey);
        if (StringUtil.isNotBlank(envRealmValue)) {
            log.tracef("Using realm config value for '%s': %s", key, envRealmValue);
            return Optional.of(envRealmValue);
        }
        String envGlobalValue = getGlobalConfig().scope(scopeParts).get(scopeKey);
        if (StringUtil.isNotBlank(envGlobalValue)) {
            log.tracef("Using global config value for '%s': %s", key, envGlobalValue);
            return Optional.of(envGlobalValue);
        }
        return Optional.empty();
    }

    public Optional<String> getFromAdminUiConfig(String key) {
        return getAdminUiConfig()
                .map(config -> config.getFirst(key));
    }

    private String get(String key, String defaultValue) {
        return getFromEnvConfig(key)
                .or(() -> getFromAdminUiConfig(key))
                .orElse(defaultValue);
    }
}

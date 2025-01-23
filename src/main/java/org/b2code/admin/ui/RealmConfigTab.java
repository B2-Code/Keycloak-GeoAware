package org.b2code.admin.ui;

import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.PluginConstants;
import org.b2code.ServerInfoAwareFactory;
import org.b2code.admin.PluginConfigOptions;
import org.b2code.admin.PluginConfigWrapper;
import org.b2code.geoip.GeoIpProvider;
import org.b2code.geoip.maxmind.MaxmindDatabase;
import org.keycloak.Config;
import org.keycloak.common.Profile;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.models.utils.PostMigrationEvent;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.provider.ProviderEvent;
import org.keycloak.services.ui.extend.UiTabProvider;
import org.keycloak.services.ui.extend.UiTabProviderFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JBossLog
@AutoService(UiTabProviderFactory.class)
public class RealmConfigTab extends ServerInfoAwareFactory implements UiTabProvider, UiTabProviderFactory<ComponentModel> {

    public static final String PROVIDER_ID = PluginConstants.PLUGIN_NAME_LOWER_CASE + "-realm-configuration-tab";

    static {
        if (!Profile.isFeatureEnabled(Profile.Feature.DECLARATIVE_UI)) {
            log.infof("As the declarative UI is not enabled, %s can not be configured in the admin console. To enable it, you will need to rebuild Keycloak and enable the '%s' feature.", PluginConstants.PLUGIN_NAME, Profile.Feature.DECLARATIVE_UI.getKey());
        }
    }

    private List<ProviderConfigProperty> configProperties;

    @Override
    public void init(Config.Scope config) {
        ProviderConfigurationBuilder builder = ProviderConfigurationBuilder.create();
        builder.property(PluginConfigOptions.ENABLED);
        builder.property(PluginConfigOptions.LOGIN_HISTORY_RETENTION_DAYS);
        builder.property(PluginConfigOptions.LOGIN_HISTORY_MAX_RECORDS);
        builder.property(PluginConfigOptions.GEOIP_PROVIDER);
        builder.property(PluginConfigOptions.GEOIP_CACHE_SIZE);
        builder.property(PluginConfigOptions.MAXMIND_FILE_PATH);
        builder.property(PluginConfigOptions.MAXMIND_ACCOUNT_ID);
        builder.property(PluginConfigOptions.MAXMIND_LICENSE_KEY);
        builder.property(PluginConfigOptions.MAXMIND_WEB_DATABASE);
        configProperties = builder.build();
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel model) throws ComponentValidationException {
        RealmConfigTabValidator.validateConfiguration(realm, model);
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        setGeoipProviderOptions(factory.create());
        setMaxmindWebDatabaseOptions();
        factory.register((ProviderEvent event) -> {
            if (event instanceof PostMigrationEvent) {
                onPostMigrationEvent(factory);
            } else if (event instanceof RealmModel.RealmPostCreateEvent reamPostCreateEvent) {
                onRealmPostCreateEvent(reamPostCreateEvent, factory);
            }
        });
    }

    private void onPostMigrationEvent(KeycloakSessionFactory factory) {
        KeycloakModelUtils.runJobInTransaction(factory, this::syncComponentModelInAllRealms);
    }

    private void onRealmPostCreateEvent(RealmModel.RealmPostCreateEvent event, KeycloakSessionFactory factory) {
        KeycloakModelUtils.runJobInTransaction(factory, session -> syncComponentModel(event.getCreatedRealm()));
    }

    private void syncComponentModelInAllRealms(KeycloakSession session) {
        session.realms().getRealmsStream().forEach(this::syncComponentModel);
    }

    private void syncComponentModel(RealmModel realm) {
        realm.getComponentsStream()
                .filter(component -> PROVIDER_ID.equals(component.getProviderId()))
                .forEach(component -> copyEnvironmentToComponentModel(component, realm));
    }

    private void setGeoipProviderOptions(KeycloakSession session) {
        Set<String> databaseProviderOptions = session.listProviderIds(GeoIpProvider.class);
        PluginConfigOptions.GEOIP_PROVIDER.setOptions(List.copyOf(databaseProviderOptions));
    }

    private void setMaxmindWebDatabaseOptions() {
        List<String> databaseProviderOptions = Arrays.stream(MaxmindDatabase.values()).map(MaxmindDatabase::getLabel).toList();
        PluginConfigOptions.MAXMIND_WEB_DATABASE.setOptions(databaseProviderOptions);
    }

    private void copyEnvironmentToComponentModel(ComponentModel model, RealmModel realm) {
        PluginConfigWrapper pluginConfig = new PluginConfigWrapper(realm);
        MultivaluedHashMap<String, String> config = model.getConfig();
        for (ProviderConfigProperty property : configProperties) {
            pluginConfig.getFromEnvConfig(property.getName()).ifPresent(value -> {
                config.putSingle(property.getName(), value);
                property.setReadOnly(true); // does not change anything in the UI at the moment
            });
        }
        realm.updateComponent(model);
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return null;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getPath() {
        return "/:realm/realm-settings/:tab?";
    }

    @Override
    public Map<String, String> getParams() {
        return Map.of("tab", PluginConstants.PLUGIN_NAME_LOWER_CASE);
    }

}
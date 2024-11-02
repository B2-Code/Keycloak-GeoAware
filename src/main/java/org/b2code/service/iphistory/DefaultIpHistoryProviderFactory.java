package org.b2code.service.iphistory;

import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.common.util.Environment;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.models.utils.PostMigrationEvent;
import org.keycloak.representations.userprofile.config.UPAttribute;
import org.keycloak.representations.userprofile.config.UPAttributePermissions;
import org.keycloak.representations.userprofile.config.UPConfig;
import org.keycloak.representations.userprofile.config.UPGroup;
import org.keycloak.userprofile.UserProfileConstants;
import org.keycloak.userprofile.UserProfileProvider;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@JBossLog
@AutoService(IpHistoryProviderFactory.class)
public class DefaultIpHistoryProviderFactory implements IpHistoryProviderFactory {

    private static final String RECORD_RETENTION_TIME_HOURS_PARAM = "retentionTimeHours";
    private static final String RECORD_RETENTION_TIME_HOURS_DEFAULT = "24";
    private Config.Scope config;

    @Override
    public DefaultIpHistoryProvider create(KeycloakSession session) {
        int retentionTimeHours = Integer.parseInt(config.get(RECORD_RETENTION_TIME_HOURS_PARAM, RECORD_RETENTION_TIME_HOURS_DEFAULT));
        return new DefaultIpHistoryProvider(session, retentionTimeHours);
    }

    @Override
    public void init(Config.Scope scope) {
        this.config = scope;
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        keycloakSessionFactory.register(event -> {
            if (event instanceof RealmModel.RealmPostCreateEvent postCreateEvent) {
                this.updateUserProfile(postCreateEvent.getKeycloakSession());
            } else if (event instanceof PostMigrationEvent) {
                KeycloakModelUtils.runJobInTransaction(keycloakSessionFactory, session -> session.getProvider(RealmProvider.class).getRealmsStream().forEach(realm -> {
                    session.getContext().setRealm(realm);
                    this.updateUserProfile(session);
                }));
            }
        });
    }

    private void updateUserProfile(KeycloakSession session) {
        UserProfileProvider userProfileProvider = session.getProvider(UserProfileProvider.class);
        UPConfig existingUpConfig = userProfileProvider.getConfiguration().clone();

        String groupName = "ip-history";
        UPGroup expectedGroup = new UPGroup(groupName);
        expectedGroup.setDisplayHeader("IP History");
        expectedGroup.setDisplayDescription("Records of IP addresses and devices used by the user");

        List<UPGroup> existingGroups = existingUpConfig.getGroups().stream()
                .filter(group -> group.getName().equals(expectedGroup.getName()))
                .toList();

        if (existingGroups.size() != 1) {
            if (existingGroups.size() > 1) {
                log.warnf("Multiple user profile groups with name '%s' found in realm '%s'", expectedGroup.getName(), session.getContext().getRealm().getName());
                List<UPGroup> groups = existingUpConfig.getGroups();
                groups.removeIf(group -> group.getName().equals(expectedGroup.getName()));
                existingUpConfig.setGroups(groups);
            }
            log.debugf("Adding user profile group '%s' to realm '%s'", expectedGroup.getName(), session.getContext().getRealm().getName());
            UPConfig newUpConfig = existingUpConfig.addGroup(expectedGroup);
            userProfileProvider.setConfiguration(newUpConfig);
        } else {
            UPGroup existingGroup = existingGroups.getFirst();
            if (!existingGroup.equals(expectedGroup)) {
                log.debugf("Updating user profile group '%s' in realm '%s'", expectedGroup.getName(), session.getContext().getRealm().getName());
                existingUpConfig.getGroups().removeIf(group -> group.getName().equals(existingGroup.getName()));
                UPConfig newUpConfig = existingUpConfig.addGroup(expectedGroup);
                userProfileProvider.setConfiguration(newUpConfig);
            }
        }

        UPAttribute expectedAttribute = new UPAttribute(DefaultIpHistoryProvider.USER_ATTRIBUTE_LAST_IPS, true, new UPAttributePermissions(Set.of(UserProfileConstants.ROLE_ADMIN), Environment.isDevMode() ? Set.of(UserProfileConstants.ROLE_ADMIN) : Collections.emptySet()));
        expectedAttribute.setDisplayName("Records");
        expectedAttribute.setGroup(groupName);
        UPAttribute existingAttribute = existingUpConfig.getAttribute(DefaultIpHistoryProvider.USER_ATTRIBUTE_LAST_IPS);
        if (existingAttribute == null || !existingAttribute.equals(expectedAttribute)) {
            log.debugf("Updating user profile attribute '%s' in realm '%s'", DefaultIpHistoryProvider.USER_ATTRIBUTE_LAST_IPS, session.getContext().getRealm().getName());
            UPConfig newUpConfig = existingUpConfig.addOrReplaceAttribute(expectedAttribute);
            userProfileProvider.setConfiguration(newUpConfig);
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

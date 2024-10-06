package org.b2code.service.iphistory;

import jakarta.validation.constraints.NotNull;
import org.keycloak.provider.Provider;

public interface IpHistoryProvider extends Provider {

    void track(@NotNull String ip, @NotNull String userId);
}

package org.b2code.authentication;

import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

@Getter
public enum UnknownIPAuthenticatorNotificationMode {
    NEVER,
    ALWAYS,
    UNKNOWN_IP,
    ON_CHANGE,
    UNKNOWN_LOCATION;

    private String label;

    private void setLabel(String label) {
        this.label = label;
    }

    static Optional<UnknownIPAuthenticatorNotificationMode> getByLabel(String label) {
        return Stream.of(values())
                .filter(mode -> mode.label.equals(label))
                .findFirst();
    }

    static {
        NEVER.setLabel("Never");
        ALWAYS.setLabel("Always");
        UNKNOWN_IP.setLabel("Unknown IP");
        ON_CHANGE.setLabel("On Change");
        UNKNOWN_LOCATION.setLabel("Unknown Location");
    }
}

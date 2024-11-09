package org.b2code.authentication;

import lombok.Getter;

@Getter
public enum NotificationMode {
    ALWAYS,
    UNKNOWN_IP,
    ON_CHANGE,
    UNKNOWN_LOCATION;

    private String label;

    private void setLabel(String label) {
        this.label = label;
    }

    static NotificationMode getByLabel(String label) {
        for (NotificationMode mode : values()) {
            if (mode.label.equals(label)) {
                return mode;
            }
        }
        return null;
    }

    static {
        ALWAYS.setLabel("Always");
        UNKNOWN_IP.setLabel("Unknown IP");
        ON_CHANGE.setLabel("On Change");
        UNKNOWN_LOCATION.setLabel("Unknown Location");
    }
}

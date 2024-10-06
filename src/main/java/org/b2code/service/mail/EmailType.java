package org.b2code.service.mail;

import lombok.Getter;

@Getter
public enum EmailType {
    LOGIN_FROM_NEW_IP("loginFromNewIpSubject", "login-from-new-ip.ftl"),
    LOGIN_FROM_NEW_DEVICE("loginFromNewDeviceSubject", "login-from-new-device.ftl");

    private final String subjectKey;
    private final String templateName;

    EmailType(String subjectKey, String templateName) {
        this.subjectKey = subjectKey;
        this.templateName = templateName;
    }

}

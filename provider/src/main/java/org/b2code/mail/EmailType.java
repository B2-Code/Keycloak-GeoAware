package org.b2code.mail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailType {

    LOGIN_FROM_NEW_IP("loginFromNewIpSubject", "login-from-new-ip.ftl"),
    LOGIN_FROM_NEW_DEVICE("loginFromNewDeviceSubject", "login-from-new-device.ftl");

    private final String subjectKey;
    private final String templateName;

}

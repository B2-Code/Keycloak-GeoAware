package org.b2code.extension.loginhistory;

import org.keycloak.testframework.injection.LifeCycle;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface InjectLoginHistory {

    LifeCycle lifecycle() default LifeCycle.CLASS;

}
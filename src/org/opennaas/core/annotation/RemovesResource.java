package org.opennaas.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <code>RemovesResource</code> is a method annotation which has to be used to
 * define a service, which removes a resource from the platform.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RemovesResource {

}

package org.mqnaas.core.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <code>DependingOn</code> is a field annotation declaring a dependency of an application implementation on another application. The services defined
 * in a specific capability implementation will only get available, if all its dependencies, e.g. all attributes annotated with
 * <code>DependingOn</code> have been successfully resolved by the platform.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DependingOn {

}

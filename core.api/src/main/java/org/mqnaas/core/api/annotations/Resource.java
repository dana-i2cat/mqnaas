package org.mqnaas.core.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mqnaas.core.api.IBindingManagement;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;

/**
 * Annotation to be used in {@link ICapability} instances field of type {@link IResource}.<br />
 * When a new <code>ICapability</code> is instantiated by {@link IBindingManagement}, first field found will be filled by the corresponding
 * <code>IResource</code> associated with.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Resource {
}

package org.mqnaas.core.api;

import org.mqnaas.core.api.annotations.DependingOn;

/**
 * <p>
 * <code>ICapability</code> is a marker interface to identify capability interfaces and their implementations.
 * </p>
 * 
 * <p>
 * Like {@link IApplication}s, <code>ICapability</code>s can depend on other capabilities to provide their services, which may be expressed by
 * annotating attributes with the {@link DependingOn} annotation. Services provided by a given capability implementation will only get available if
 * all capability dependencies are resolved.
 * </p>
 * 
 * TODO Define a mechanism that can be used to initialize capabilities after dependency resolution (see {@link IApplication} for another use-case of
 * this initialization mechanism).
 */
public interface ICapability extends IApplication {
}

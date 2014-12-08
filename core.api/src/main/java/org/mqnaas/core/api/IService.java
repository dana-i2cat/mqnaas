package org.mqnaas.core.api;

/**
 * The <code>IService</code>
 */
public interface IService extends IIdentifiable {

	IResource getResource();

	IServiceMetaData getMetadata();

}

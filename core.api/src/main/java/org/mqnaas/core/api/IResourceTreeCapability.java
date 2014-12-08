package org.mqnaas.core.api;

public interface IResourceTreeCapability extends ICapability {

	IApplication getInstance(Class<? extends IApplication> applicationClazz);

}

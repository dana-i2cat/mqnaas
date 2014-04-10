package org.opennaas.core.impl;

import java.util.Set;

import org.opennaas.core.api.IApplication;
import org.osgi.framework.Bundle;

public class ApplicationManagement {

	public Set<Class<? extends IApplication>> addBundle(Bundle bundle) {
		
		// Scan all class resources of the given bundle and collects all
		// applications classes, e.g. those which implement IApplication		
		return BundleUtils.scanBundle(bundle, IApplication.class);
	}
	
}

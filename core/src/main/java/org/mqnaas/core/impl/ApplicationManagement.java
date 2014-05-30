package org.mqnaas.core.impl;

import java.util.Set;

import org.mqnaas.bundletree.utils.BundleClassPathUtils;
import org.mqnaas.core.api.IApplication;
import org.osgi.framework.Bundle;

public class ApplicationManagement {

	public Set<Class<? extends IApplication>> addBundle(Bundle bundle) {
		// Filter bundles depending on core and scan all class resources of the given bundle and collects all capability classes, e.g. those which
		// implement IApplication
		return BundleClassPathUtils.scanBundle(bundle, IApplication.class);
	}

}

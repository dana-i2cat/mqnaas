package org.mqnaas.core.impl.dependencies.samples._7_multipleimpl;

import java.util.ArrayList;
import java.util.Collection;

import org.mqnaas.core.impl.dependencies.samples.IApp;

public class ScenarioInitializer {

	public static Collection<IApp> getInstances() {
		ArrayList<IApp> apps = new ArrayList<IApp>(3);
		apps.add(new AppA());
		apps.add(new AppB1());
		apps.add(new AppB2());

		return apps;
	}

}

package org.mqnaas.core.impl.dependencies.samples._5_minicycle;

import java.util.ArrayList;
import java.util.Collection;

import org.mqnaas.core.impl.dependencies.samples.IApp;

public class ScenarioInitializer {

	public static Collection<IApp> getInstances() {
		ArrayList<IApp> apps = new ArrayList<IApp>(2);
		apps.add(new AppA());
		apps.add(new AppB());
		return apps;
	}

}

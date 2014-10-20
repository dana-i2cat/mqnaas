package org.mqnaas.core.impl.dependencies.samples._3_cycle;

import java.util.ArrayList;
import java.util.Collection;

import org.mqnaas.core.impl.dependencies.samples.IApp;

public class ScenarioInitializer {

	public static Collection<IApp> getInstances() {
		ArrayList<IApp> apps = new ArrayList<IApp>(3);
		apps.add(new AppA());
		apps.add(new AppB());
		apps.add(new AppC());
		return apps;
	}

}

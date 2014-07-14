package org.mqnaas.core.impl.dependencies.samples._6_4cycle;

import java.util.ArrayList;
import java.util.Collection;

import org.mqnaas.core.impl.dependencies.samples.IApp;

public class ScenarioInitializer {

	public static Collection<IApp> getInstances() {
		ArrayList<IApp> apps = new ArrayList<IApp>(5);
		apps.add(new AppA());
		apps.add(new AppB());
		apps.add(new AppC());
		apps.add(new AppD());
		apps.add(new AppE());
		return apps;
	}

}

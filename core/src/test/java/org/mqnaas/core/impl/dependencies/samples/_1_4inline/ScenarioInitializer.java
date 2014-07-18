package org.mqnaas.core.impl.dependencies.samples._1_4inline;

import java.util.ArrayList;
import java.util.Collection;

import org.mqnaas.core.impl.dependencies.samples.IApp;

public class ScenarioInitializer {

	public static Collection<IApp> getInstances() {
		ArrayList<IApp> apps = new ArrayList<IApp>(4);
		apps.add(new AppA());
		apps.add(new AppB());
		apps.add(new AppC());
		apps.add(new AppD());
		return apps;
	}

}

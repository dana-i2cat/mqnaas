package org.mqnaas.core.impl.dependencies.samples._4_multicycle;

import java.util.ArrayList;
import java.util.Collection;

import org.mqnaas.core.impl.dependencies.samples.IApp;

public class ScenarioInitializer {

	public static Collection<IApp> getInstances() {
		ArrayList<IApp> apps = new ArrayList<IApp>(4);
		apps.add(new AppAF());
		apps.add(new AppB());
		apps.add(new AppC());
		apps.add(new AppDE());
		return apps;
	}

}

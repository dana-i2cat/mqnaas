package org.mqnaas.core.impl.dependencies.samples._3_cycle;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.samples.App;
import org.mqnaas.core.impl.dependencies.samples.IAppB;
import org.mqnaas.core.impl.dependencies.samples.IAppC;

public class AppB extends App implements IAppB {

	@DependingOn
	IAppC	appC;

	@Override
	public void b() {
		// TODO Auto-generated method stub

	}

}

package org.mqnaas.core.impl.dependencies.samples._5_minicycle;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.samples.App;
import org.mqnaas.core.impl.dependencies.samples.IAppA;
import org.mqnaas.core.impl.dependencies.samples.IAppB;

public class AppB extends App implements IAppB {

	@DependingOn
	IAppA	appA;

	@Override
	public void b() {
		// TODO Auto-generated method stub

	}

}

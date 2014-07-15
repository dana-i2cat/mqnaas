package org.mqnaas.core.impl.dependencies.samples._1_4inline;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.samples.App;
import org.mqnaas.core.impl.dependencies.samples.IAppA;
import org.mqnaas.core.impl.dependencies.samples.IAppB;

public class AppB extends App implements IAppB {

	@DependingOn
	IAppA	a;

	@Override
	public void b() {
		// TODO Auto-generated method stub
	}

}

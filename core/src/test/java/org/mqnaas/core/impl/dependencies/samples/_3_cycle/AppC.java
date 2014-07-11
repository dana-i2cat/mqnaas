package org.mqnaas.core.impl.dependencies.samples._3_cycle;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.samples.App;
import org.mqnaas.core.impl.dependencies.samples.IAppC;
import org.mqnaas.core.impl.dependencies.samples.IAppE;

public class AppC extends App implements IAppC {

	@DependingOn
	IAppE	appA;

	@Override
	public void c() {
		// TODO Auto-generated method stub

	}

}

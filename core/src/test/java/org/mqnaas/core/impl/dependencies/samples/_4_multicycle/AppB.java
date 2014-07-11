package org.mqnaas.core.impl.dependencies.samples._4_multicycle;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.samples.App;
import org.mqnaas.core.impl.dependencies.samples.IAppB;
import org.mqnaas.core.impl.dependencies.samples.IAppD;

public class AppB extends App implements IAppB {

	@DependingOn
	IAppD	appD;

	@Override
	public void b() {
		// TODO Auto-generated method stub

	}

}

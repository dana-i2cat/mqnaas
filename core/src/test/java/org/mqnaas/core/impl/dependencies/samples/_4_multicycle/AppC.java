package org.mqnaas.core.impl.dependencies.samples._4_multicycle;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.samples.App;
import org.mqnaas.core.impl.dependencies.samples.IAppC;
import org.mqnaas.core.impl.dependencies.samples.IAppE;

public class AppC extends App implements IAppC {

	@DependingOn
	IAppE	appE;

	@Override
	public void c() {
		// TODO Auto-generated method stub

	}

}

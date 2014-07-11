package org.mqnaas.core.impl.dependencies.samples._4_multicycle;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.samples.App;
import org.mqnaas.core.impl.dependencies.samples.IAppD;
import org.mqnaas.core.impl.dependencies.samples.IAppE;
import org.mqnaas.core.impl.dependencies.samples.IAppF;

public class AppDE extends App implements IAppD, IAppE {

	@DependingOn
	IAppF	appF;

	@Override
	public void e() {
		// TODO Auto-generated method stub

	}

	@Override
	public void d() {
		// TODO Auto-generated method stub

	}

}

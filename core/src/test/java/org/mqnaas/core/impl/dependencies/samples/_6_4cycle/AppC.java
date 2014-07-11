package org.mqnaas.core.impl.dependencies.samples._6_4cycle;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.samples.App;
import org.mqnaas.core.impl.dependencies.samples.IAppC;
import org.mqnaas.core.impl.dependencies.samples.IAppD;

public class AppC extends App implements IAppC {

	@DependingOn
	IAppD	appD;

	@Override
	public void c() {
		// TODO Auto-generated method stub

	}

}

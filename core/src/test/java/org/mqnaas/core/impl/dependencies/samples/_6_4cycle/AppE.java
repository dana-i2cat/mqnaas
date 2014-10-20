package org.mqnaas.core.impl.dependencies.samples._6_4cycle;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.samples.App;
import org.mqnaas.core.impl.dependencies.samples.IAppB;
import org.mqnaas.core.impl.dependencies.samples.IAppE;

public class AppE extends App implements IAppE {

	@DependingOn
	IAppB	appB;

	@Override
	public void e() {
		// TODO Auto-generated method stub

	}

}

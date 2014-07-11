package org.mqnaas.core.impl.dependencies.samples._1_4inline;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.samples.App;
import org.mqnaas.core.impl.dependencies.samples.IAppC;
import org.mqnaas.core.impl.dependencies.samples.IAppD;

public class AppD extends App implements IAppD {

	@DependingOn
	IAppC	c;

	@Override
	public void d() {
		// TODO Auto-generated method stub
	}

}

package org.mqnaas.core.impl.dependencies.samples._1_4inline;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.samples.App;
import org.mqnaas.core.impl.dependencies.samples.IAppB;
import org.mqnaas.core.impl.dependencies.samples.IAppC;

public class AppC extends App implements IAppC {

	@DependingOn
	IAppB	b;

	@Override
	public void c() {
		// TODO Auto-generated method stub
	}

}

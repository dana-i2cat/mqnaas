package org.mqnaas.core.impl.dependencies.samples._6_4cycle;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.samples.App;
import org.mqnaas.core.impl.dependencies.samples.IAppA;
import org.mqnaas.core.impl.dependencies.samples.IAppB;
import org.mqnaas.core.impl.dependencies.samples.IAppD;

public class AppA extends App implements IAppA {

	@DependingOn
	IAppB	appB;

	@DependingOn
	IAppD	appD;

	@Override
	public void a() {
		// TODO Auto-generated method stub

	}
}

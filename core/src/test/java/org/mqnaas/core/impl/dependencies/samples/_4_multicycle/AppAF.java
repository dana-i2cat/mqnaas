package org.mqnaas.core.impl.dependencies.samples._4_multicycle;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.samples.App;
import org.mqnaas.core.impl.dependencies.samples.IAppA;
import org.mqnaas.core.impl.dependencies.samples.IAppB;
import org.mqnaas.core.impl.dependencies.samples.IAppC;
import org.mqnaas.core.impl.dependencies.samples.IAppD;
import org.mqnaas.core.impl.dependencies.samples.IAppF;

public class AppAF extends App implements IAppA, IAppF {

	@DependingOn
	IAppB	appB;

	@DependingOn
	IAppC	appC;

	@DependingOn
	IAppD	appD;

	@Override
	public void f() {
		// TODO Auto-generated method stub

	}

	@Override
	public void a() {
		// TODO Auto-generated method stub

	}

}

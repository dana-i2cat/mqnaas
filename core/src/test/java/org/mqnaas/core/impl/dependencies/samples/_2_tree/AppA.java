package org.mqnaas.core.impl.dependencies.samples._2_tree;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.samples.App;
import org.mqnaas.core.impl.dependencies.samples.IAppA;
import org.mqnaas.core.impl.dependencies.samples.IAppB;
import org.mqnaas.core.impl.dependencies.samples.IAppC;
import org.mqnaas.core.impl.dependencies.samples.IAppD;

public class AppA extends App implements IAppA {

	@DependingOn
	IAppB	appB;

	@DependingOn
	IAppC	appC;

	@DependingOn
	IAppD	appD;

	@Override
	public void a() {
		// TODO Auto-generated method stub

	}

}

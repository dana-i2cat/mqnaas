package org.mqnaas.core.impl.dependencies.samples._7_multipleimpl;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.samples.App;
import org.mqnaas.core.impl.dependencies.samples.IAppA;
import org.mqnaas.core.impl.dependencies.samples.IAppB;

public class AppA extends App implements IAppA {

	@DependingOn
	IAppB	appB;

	@Override
	public void a() {
		// TODO Auto-generated method stub

	}

}
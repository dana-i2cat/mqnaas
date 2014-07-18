package org.mqnaas.core.impl.dependencies.samples._2_tree;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.samples.App;
import org.mqnaas.core.impl.dependencies.samples.IAppD;
import org.mqnaas.core.impl.dependencies.samples.IAppE;

public class AppD extends App implements IAppD {

	@DependingOn
	IAppE	appE;

	@Override
	public void d() {
		// TODO Auto-generated method stub

	}

}

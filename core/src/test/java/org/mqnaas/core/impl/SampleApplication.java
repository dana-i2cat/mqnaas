package org.mqnaas.core.impl;

import org.mqnaas.core.api.IApplication;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class SampleApplication implements IApplication {

	@Override
	public void onDependenciesResolved() {
		System.out.println("SampleApplication resolved!");
	}

}

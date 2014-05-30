package org.mqnaas.bundletree.itests.testbundleB;

import org.mqnaas.bundletree.itests.testbundleA.RootInterface;

public class TestClassB implements RootInterface {

	@Override
	public void onDependenciesResolved() {
		System.out.println("Dependencies resolved on TestClassB");
	}

}

package org.opennaas.core.impl;

import org.opennaas.core.api.ITransactionBehavior;

public class UnawareTransactionBehaviour implements ITransactionBehavior {

	@Override
	public void begin() {
	}

	@Override
	public void rollback() {
	}

	@Override
	public void commit() {
	}

}

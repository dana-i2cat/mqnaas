package org.mqnaas.core.impl;

import org.mqnaas.core.api.ITransactionBehavior;

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

package org.mqnaas.core.api;

/**
 * <code>ITransactionBehavior</code> defines the way in which a resource participates in a transaction.
 */
public interface ITransactionBehavior {

	void begin();

	void rollback();

	void commit();

}

package org.opennaas.core.api;

/**
 * <p>
 * <code>ILockingBehaviour</code> defines the locking behavior of a
 * {@link IRootResource} in OpenNaaS.
 * </p>
 * <p>
 * A resource may
 * <ol>
 * <li>utilize an integrated locking mechanism,</li>
 * <li>the OpenNaaS internal locking mechanism (which is the default), or</li>
 * <li>can implement it's own behavior by implementing this interface.</li>
 * </ol>
 */
public interface ILockingBehaviour {

	boolean lock(ExecutionContext executionContext, IRootResource resource);

	boolean isLocked(ExecutionContext executionContext, IRootResource resource);

	boolean unlock(ExecutionContext executionContext, IRootResource resource);

}

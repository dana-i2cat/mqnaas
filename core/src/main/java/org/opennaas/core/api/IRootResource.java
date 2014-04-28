package org.opennaas.core.api;

/**
 * <p>
 * <code>IRootResource</code> is the representation of a physical device in OpenNaaS.
 * </p>
 * <p>
 * In contrast to other resources, a root resource defines its
 * <ol>
 * <li>transaction behavior (see {@link ITransactionBehavior}), its</li>
 * <li>locking behavior (see {@link ILockingBehaviour}) and its</li>
 * <li>technical specification.</li>
 * </ol>
 * 
 * <p>
 * The transaction behavior defines how a resource participates in transactions, the locking behavior specifies how the resource can be locked to
 * avoid concurrent usage.
 * </p>
 * 
 * <p>
 * The technical specification can be used by the resource-capability binding mechanism to determine whether a resource and a given capability
 * implementation should be bound (see {@link IBindingManagement#shouldBeBound(IResource, Class)}.
 * </p>
 */
public interface IRootResource extends IResource {

	ITransactionBehavior getTransactionBehaviour();

	ILockingBehaviour getLockingBehaviour();

	Specification getSpecification();

}

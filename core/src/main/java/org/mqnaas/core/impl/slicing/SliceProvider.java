package org.mqnaas.core.impl.slicing;

import java.lang.reflect.Method;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.slicing.ISliceProvider;

/**
 * Implementation of the {@link ISliceProvider} capability that at the moment only binds to {@link Type#TSON}s.
 * 
 * On activation the slice resource is created and bound.
 * 
 * @author Georg Mansky-Kummert
 */
public class SliceProvider implements ISliceProvider {

	private IResource					slice;

	@DependingOn
	private IResourceManagementListener	resourceManagementListener;

	@Override
	public IResource getSlice() {
		return slice;
	}

	public static boolean isSupporting(IRootResource resource) {
		return resource.getDescriptor().getSpecification().getType() == Type.TSON;
	}

	public static boolean isSupporting(IResource resource) {
		boolean isSupporting = false;

		// FIXME Needs to be refactoring once the binding mechanism was reworked
		if (resource.getClass().getName().equals("org.mqnaas.network.impl.RequestRootResource")) {

			try {
				Method m = resource.getClass().getMethod("getType");

				Type type = (Type) m.invoke(resource);

				isSupporting = type.equals(Type.TSON);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return isSupporting;
	}

	@Override
	public void activate() {
		// TODO: persistence
		slice = new SliceResource();

		// Add resource manually to the platform
		resourceManagementListener.resourceAdded(slice, this, ISliceProvider.class);
	}

	@Override
	public void deactivate() {
		// TODO: persistence

		// Remove resource manually from the platform
		resourceManagementListener.resourceRemoved(slice, this, ISliceProvider.class);
	}

}

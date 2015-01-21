package org.mqnaas.core.impl.slicing;

import java.lang.reflect.Method;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.slicing.ISliceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link ISliceProvider} capability that at the moment only binds to {@link Type#TSON}s.
 * 
 * On activation the slice resource is created and bound.
 * 
 * @author Georg Mansky-Kummert
 */
public class SliceProvider implements ISliceProvider {

	private static final Logger			log	= LoggerFactory.getLogger(SliceProvider.class);

	private IResource					slice;

	@Resource
	private IResource					resource;

	@DependingOn
	private IResourceManagementListener	resourceManagementListener;

	@Override
	public IResource getSlice() {
		return slice;
	}

	public static boolean isSupporting(IRootResource resource) {
		Type resourceType = resource.getDescriptor().getSpecification().getType();

		return resourceType.equals(Type.TSON) || resourceType.equals(Type.OF_SWITCH) || resourceType.equals(Type.CPE) || resourceType
				.equals(Type.ARN);
	}

	public static boolean isSupporting(IResource resource) {
		boolean isSupporting = false;

		// FIXME Needs to be refactoring once the binding mechanism was reworked
		if (resource.getClass().getName().equals("org.mqnaas.network.impl.request.RequestRootResource")) {

			try {
				Method m = resource.getClass().getMethod("getType");

				Type type = (Type) m.invoke(resource);

				isSupporting = type.equals(Type.TSON) || type.equals(type.OF_SWITCH) || type.equals(type.ARN) || type.equals(type.CPE);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return isSupporting;
	}

	@Override
	public void activate() throws ApplicationActivationException {

		log.info("Initializing SliceProvider capability for resource " + resource.getId());
		// TODO: persistence
		slice = new SliceResource();

		log.debug("Created " + slice.getId() + " in resource " + resource.getId());

		// Add resource manually to the platform
		resourceManagementListener.resourceAdded(slice, this, ISliceProvider.class);

		log.info("Initialized SliceProvider capability for resource " + resource.getId());

	}

	@Override
	public void deactivate() {
		log.info("Removing SliceProvider capability for resource " + resource.getId());

		// TODO: persistence

		// Remove resource manually from the platform
		resourceManagementListener.resourceRemoved(slice, this, ISliceProvider.class);

		slice = null;

		log.info("Removed SliceProvider capability for resource " + resource.getId());

	}
}

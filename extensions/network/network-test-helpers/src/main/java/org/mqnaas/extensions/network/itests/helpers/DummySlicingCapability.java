package org.mqnaas.extensions.network.itests.helpers;

import java.util.ArrayList;
import java.util.Collection;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.slicing.ISlicingCapability;
import org.mqnaas.core.api.slicing.SlicingException;
import org.mqnaas.core.impl.RootResource;

public class DummySlicingCapability implements ISlicingCapability {

	@Resource
	IRootResource			resource;

	Collection<IResource>	slices;

	public static boolean isSupporting(IRootResource resource) {
		Type type = resource.getDescriptor().getSpecification().getType();
		return ((type != Type.NETWORK) && (type != Type.CORE));
	}

	@Override
	public void activate() throws ApplicationActivationException {
		slices = new ArrayList<IResource>();
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

	@Override
	public IResource createSlice(IResource slice) throws SlicingException {

		try {
			IRootResource createdResource = new RootResource(RootResourceDescriptor.create(resource.getDescriptor().getSpecification()
					.clone(), resource.getDescriptor().getEndpoints()));
			createdResource.getDescriptor().getSpecification().setModel("virtual");

			slices.add(createdResource);

			return createdResource;

		} catch (Exception e) {
			throw new SlicingException(e);
		}

	}

	@Override
	public void removeSlice(IResource rootResource) throws SlicingException {
		slices.remove(rootResource);
	}

	@Override
	public Collection<IResource> getSlices() {
		return slices;
	}
}

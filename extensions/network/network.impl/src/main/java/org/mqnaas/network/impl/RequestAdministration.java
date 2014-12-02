package org.mqnaas.network.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.network.IRequestAdministration;
import org.mqnaas.core.api.network.ISliceAssignment;
import org.mqnaas.core.api.network.Period;

/**
 * Implementation of the {@link IRequestAdministration} and
 * {@link ISliceAssignment} capabilities, which is backed by a {@link ConcurrentHashMap} and bound to a
 * {@link RequestResource}.
 * 
 * @author Georg Mansky-Kummert
 */
public class RequestAdministration implements IRequestAdministration,
		ISliceAssignment {

	public static boolean isSupporting(IResource resource) {
		return resource instanceof RequestResource;
	}

	private Period period;

	private Map<IResource, IResource> slices;

	@Override
	public void defineSlice(IResource rootResource, IResource slice) {
		slices.put(rootResource, slice);
	}

	@Override
	public void removeSlice(IResource rootResource) {
		slices.remove(rootResource);
	}

	@Override
	public IResource getSlice(IResource rootResource) {
		return slices.get(rootResource);
	}

	@Override
	public Collection<IResource> getSlices() {
		return slices.keySet();
	}

	@Override
	public void setPeriod(Period period) {
		this.period = period;
	}

	@Override
	public Period getPeriod() {
		return period;
	}

	@Override
	public void activate() {
		slices = new ConcurrentHashMap<IResource, IResource>();
	}

	@Override
	public void deactivate() {
	}

}

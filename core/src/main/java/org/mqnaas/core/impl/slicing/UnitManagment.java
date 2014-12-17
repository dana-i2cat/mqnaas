package org.mqnaas.core.impl.slicing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.slicing.IUnitManagement;

/**
 * This implementation of the {@link IUnitManagement} is backed by a {@link CopyOnWriteArrayList} and bound to {@link SliceResource}s.
 * 
 * @author Georg Mansky-Kummert
 */
public class UnitManagment implements IUnitManagement {

	private List<UnitResource>	units;

	@Override
	public void activate() {
		units = new CopyOnWriteArrayList<UnitResource>();
	}

	@Override
	public void deactivate() {
	}

	@Override
	public IResource createUnit(String name) {
		UnitResource unit = new UnitResource(name);
		units.add(unit);
		return unit;
	}

	@Override
	public void removeUnit(IResource unit) {
		units.remove(unit);
	}

	@Override
	public List<IResource> getUnits() {
		return new ArrayList<IResource>(units);
	}

	public static boolean isSupporting(IResource resource) {
		return resource instanceof SliceResource;
	}

}

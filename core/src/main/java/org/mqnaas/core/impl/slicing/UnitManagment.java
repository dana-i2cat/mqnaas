package org.mqnaas.core.impl.slicing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.slicing.IUnitManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation of the {@link IUnitManagement} is backed by a {@link CopyOnWriteArrayList} and bound to {@link SliceResource}s.
 * 
 * @author Georg Mansky-Kummert
 */
public class UnitManagment implements IUnitManagement {

	private static final Logger	log	= LoggerFactory.getLogger(UnitManagment.class);

	private List<UnitResource>	units;

	@Resource
	IResource					resource;

	@Override
	public void activate() throws ApplicationActivationException {
		log.info("Initializing UnitManagment capability for resource " + resource.getId());
		units = new CopyOnWriteArrayList<UnitResource>();
		log.info("Initialized UnitManagment capability for resource " + resource.getId());

	}

	@Override
	public void deactivate() {
		log.info("Removing UnitManagment capability for resource " + resource.getId());
		units = new CopyOnWriteArrayList<UnitResource>();
		log.info("Removed UnitManagment capability for resource " + resource.getId());
	}

	@Override
	public IResource createUnit(String name) {
		if (StringUtils.isEmpty(name))
			throw new IllegalArgumentException("Slice unit require a valid name.");

		for (UnitResource unitResource : units)
			if (unitResource.getName().equals(name))
				throw new IllegalStateException("There already exist a slice unit with name " + name);

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

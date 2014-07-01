package org.mqnaas.core.impl;

import org.mqnaas.core.api.IResource;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class SampleResource implements IResource {

	@Override
	public String getId() {
		return "sample.resource";
	}

}

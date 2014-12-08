package org.mqnaas.examples.sampleresource;

import org.mqnaas.core.api.IResource;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class SampleResource implements IResource {
	
	private String id;
	
	public SampleResource() {
		super();
		this.id = "SampleResource";
	}
	
	public SampleResource(String id) {
		super();
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

}

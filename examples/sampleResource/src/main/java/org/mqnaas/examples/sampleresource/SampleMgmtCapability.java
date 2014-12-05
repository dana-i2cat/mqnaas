package org.mqnaas.examples.sampleresource;

import java.util.ArrayList;
import java.util.List;

import org.mqnaas.core.api.IResource;

public class SampleMgmtCapability implements ISampleMgmtCapability {

	private List<SampleResource>	resources	= new ArrayList<SampleResource>();

	@Override
	public void addSampleResource(SampleResource resource) {
		resources.add(resource);
	}

	@Override
	public void removeSampleResource(SampleResource resource) {
		resources.remove(resource);

	}

	@Override
	public List<SampleResource> getSampleResources() {
		return new ArrayList<SampleResource>(resources);
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}
	
	public static boolean isSupporting(IResource resource) {
		return true;
	}

}

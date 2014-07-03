package org.mqnaas.core.impl;

import java.util.List;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.core.api.annotations.RemovesResource;

public interface ISampleMgmtCapability extends ICapability {

	@AddsResource
	public void addSampleResource(SampleResource resource);

	@RemovesResource
	public void removeSampleResource(SampleResource resource);

	public List<SampleResource> getSampleResources();

}

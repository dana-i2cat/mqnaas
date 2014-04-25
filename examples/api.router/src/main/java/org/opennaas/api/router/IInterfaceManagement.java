package org.opennaas.api.router;

import java.util.List;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.core.api.annotations.RemovesResource;

public interface IInterfaceManagement extends ICapability {

	@AddsResource
	IInterface createInterface(String name);

	@AddsResource
	void addInterface(IInterface interfaze);

	@RemovesResource
	void removeInterface(IInterface interfaze);

	List<IInterface> getInterfaces();
}

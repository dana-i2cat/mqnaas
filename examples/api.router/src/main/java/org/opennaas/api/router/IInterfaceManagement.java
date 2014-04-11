package org.opennaas.api.router;

import java.util.List;

import org.opennaas.core.annotation.AddsResource;
import org.opennaas.core.annotation.RemovesResource;
import org.opennaas.core.api.ICapability;

public interface IInterfaceManagement extends ICapability {

	@AddsResource 
	IInterface createInterface(String name);

	@AddsResource
	void addInterface(IInterface interfaze);

	@RemovesResource
	void removeInterface(IInterface interfaze);

	List<IInterface> getInterfaces();
}

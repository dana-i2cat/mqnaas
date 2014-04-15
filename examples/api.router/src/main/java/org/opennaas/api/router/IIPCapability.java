package org.opennaas.api.router;

import org.opennaas.core.api.ICapability;

public interface IIPCapability extends ICapability {
	
	void setIP(IPProtocolEndpoint iPProtocolEndpoint);
	
	void addIP(IPProtocolEndpoint iPProtocolEndpoint);

	void removeIP(IPProtocolEndpoint iProtocolEndpoint);

}

package org.mqnaas.examples.api.router;

import org.mqnaas.core.api.ICapability;

public interface IIPCapability extends ICapability {

	void setIP(IPProtocolEndpoint iPProtocolEndpoint);

	void addIP(IPProtocolEndpoint iPProtocolEndpoint);

	void removeIP(IPProtocolEndpoint iProtocolEndpoint);

}

package org.mqnaas.extensions.odl.client.hellium.flowprogrammer.api.model;


import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "list")
@XmlAccessorType(XmlAccessType.NONE)
public class FlowConfigs {
	@XmlElement
	List<FlowConfig> flowConfig;

	// To satisfy JAXB
	@SuppressWarnings("unused")
	private FlowConfigs() {
	}

	public FlowConfigs(List<FlowConfig> flowConfig) {
		this.flowConfig = flowConfig;
	}

	public List<FlowConfig> getFlowConfig() {
		return flowConfig;
	}

	public void setFlowConfig(List<FlowConfig> flowConfig) {
		this.flowConfig = flowConfig;
	}
}
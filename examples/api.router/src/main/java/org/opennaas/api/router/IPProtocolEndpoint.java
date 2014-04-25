package org.opennaas.api.router;

public class IPProtocolEndpoint {

	private String	iPv4Address;
	private String	iPv6Address;

	private String	subnetMask;
	private short	prefixLength;

	public String getiPv4Address() {
		return iPv4Address;
	}

	public void setiPv4Address(String iPv4Address) {
		this.iPv4Address = iPv4Address;
	}

	public String getiPv6Address() {
		return iPv6Address;
	}

	public void setiPv6Address(String iPv6Address) {
		this.iPv6Address = iPv6Address;
	}

	public String getSubnetMask() {
		return subnetMask;
	}

	public void setSubnetMask(String subnetMask) {
		this.subnetMask = subnetMask;
	}

	public short getPrefixLength() {
		return prefixLength;
	}

	public void setPrefixLength(short prefixLength) {
		this.prefixLength = prefixLength;
	}

}

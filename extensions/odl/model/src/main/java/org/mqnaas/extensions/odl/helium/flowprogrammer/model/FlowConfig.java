package org.mqnaas.extensions.odl.helium.flowprogrammer.model;

/*
 * #%L
 * MQNaaS :: ODL Model
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i
 * 			Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Configuration Java Object which represents a flow configuration information
 * for Forwarding Rules Manager.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class FlowConfig {
   
    private boolean dynamic;
    private String status;

    /*
     * The order of the object data defined below is used directly in the UI
     * built using JSP. Hence try to keep the order in a more logical way.
     */
    @XmlElement
    private String installInHw;
    @XmlElement
    private String name;
    @XmlElement
    private Node node;
    @XmlElement
    private String ingressPort;
    private String portGroup;
    @XmlElement
    private String priority;
    @XmlElement
    private String etherType;
    @XmlElement
    private String vlanId;
    @XmlElement
    private String vlanPriority;
    @XmlElement
    private String dlSrc;
    @XmlElement
    private String dlDst;
    @XmlElement
    private String nwSrc;
    @XmlElement
    private String nwDst;
    @XmlElement
    private String protocol;
    @XmlElement
    private String tosBits;
    @XmlElement
    private String tpSrc;
    @XmlElement
    private String tpDst;
    @XmlElement
    private String cookie;
    @XmlElement
    private String idleTimeout;
    @XmlElement
    private String hardTimeout;
    @XmlElement
    private List<String> actions;

    public FlowConfig() {
    }

    public FlowConfig(String installInHw, String name, Node node, String priority, String cookie, String ingressPort,
            String portGroup, String vlanId, String vlanPriority, String etherType, String srcMac, String dstMac,
            String protocol, String tosBits, String srcIP, String dstIP, String tpSrc, String tpDst,
            String idleTimeout, String hardTimeout, List<String> actions) {
        super();
        this.installInHw = installInHw;
        this.name = name;
        this.node = node;
        this.priority = priority;
        this.cookie = cookie;
        this.ingressPort = ingressPort;
        this.portGroup = portGroup;
        this.vlanId = vlanId;
        this.vlanPriority = vlanPriority;
        this.etherType = etherType;
        this.dlSrc = srcMac;
        this.dlDst = dstMac;
        this.protocol = protocol;
        this.tosBits = tosBits;
        this.nwSrc = srcIP;
        this.nwDst = dstIP;
        this.tpSrc = tpSrc;
        this.tpDst = tpDst;
        this.idleTimeout = idleTimeout;
        this.hardTimeout = hardTimeout;
        this.actions = actions;
        this.status = "success";
    }

    public FlowConfig(FlowConfig from) {
        this.installInHw = from.installInHw;
        this.name = from.name;
        this.node = from.node;
        this.priority = from.priority;
        this.cookie = from.cookie;
        this.ingressPort = from.ingressPort;
        this.portGroup = from.portGroup;
        this.vlanId = from.vlanId;
        this.vlanPriority = from.vlanPriority;
        this.etherType = from.etherType;
        this.dlSrc = from.dlSrc;
        this.dlDst = from.dlDst;
        this.protocol = from.protocol;
        this.tosBits = from.tosBits;
        this.nwSrc = from.nwSrc;
        this.nwDst = from.nwDst;
        this.tpSrc = from.tpSrc;
        this.tpDst = from.tpDst;
        this.idleTimeout = from.idleTimeout;
        this.hardTimeout = from.hardTimeout;
        this.actions = new ArrayList<String>(from.actions);
    }

    public boolean installInHw() {
        if (installInHw == null) {
            // backward compatibility
            installInHw = Boolean.toString(true);
        }
        return Boolean.valueOf(installInHw);
    }

    public void setInstallInHw(boolean inHw) {
        installInHw = String.valueOf(inHw);
    }

    public String getInstallInHw() {
        return installInHw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            return;
        }
        this.name = name;
    }

    public Node getNode() {
        return this.node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getIngressPort() {
        return ingressPort;
    }

    public void setIngressPort(String ingressPort) {
        this.ingressPort = ingressPort;
    }

    public String getPortGroup() {
        return portGroup;
    }

    public void setPortGroup(String portGroup) {
        this.portGroup = portGroup;
    }

    public String getVlanId() {
        return vlanId;
    }

    public void setVlanId(String vlanId) {
        this.vlanId = vlanId;
    }

    public String getVlanPriority() {
        return vlanPriority;
    }

    public void setVlanPriority(String vlanPriority) {
        this.vlanPriority = vlanPriority;
    }

    public String getEtherType() {
        return etherType;
    }

    public void setEtherType(String etherType) {
        this.etherType = etherType;
    }

    public String getSrcMac() {
        return dlSrc;
    }

    public void setSrcMac(String srcMac) {
        this.dlSrc = srcMac;
    }

    public String getDstMac() {
        return dlDst;
    }

    public void setDstMac(String dstMac) {
        this.dlDst = dstMac;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getTosBits() {
        return tosBits;
    }

    public void setTosBits(String tos_bits) {
        this.tosBits = tos_bits;
    }

    public String getSrcIp() {
        return nwSrc;
    }

    public void setSrcIp(String src_ip) {
        this.nwSrc = src_ip;
    }

    public String getDstIp() {
        return nwDst;
    }

    public void setDstIp(String dst_ip) {
        this.nwDst = dst_ip;
    }

    public String getSrcPort() {
        return tpSrc;
    }

    public void setSrcPort(String src_port) {
        this.tpSrc = src_port;
    }

    public String getDstPort() {
        return tpDst;
    }

    public void setDstPort(String dst_port) {
        this.tpDst = dst_port;
    }

    public String getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(String idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public String getHardTimeout() {
        return hardTimeout;
    }

    public void setHardTimeout(String hardTimeout) {
        this.hardTimeout = hardTimeout;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public boolean isPortGroupEnabled() {
        return (portGroup != null);
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((actions == null) ? 0 : actions.hashCode());
        result = prime * result + ((cookie == null) ? 0 : cookie.hashCode());
        result = prime * result + ((dlDst == null) ? 0 : dlDst.hashCode());
        result = prime * result + ((dlSrc == null) ? 0 : dlSrc.hashCode());
        result = prime * result + (dynamic ? 1231 : 1237);
        result = prime * result + ((etherType == null) ? 0 : etherType.hashCode());
        result = prime * result + ((ingressPort == null) ? 0 : ingressPort.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((nwDst == null) ? 0 : nwDst.hashCode());
        result = prime * result + ((nwSrc == null) ? 0 : nwSrc.hashCode());
        result = prime * result + ((portGroup == null) ? 0 : portGroup.hashCode());
        result = prime * result + ((priority == null) ? 0 : priority.hashCode());
        result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
        result = prime * result + ((node == null) ? 0 : node.hashCode());
        result = prime * result + ((tosBits == null) ? 0 : tosBits.hashCode());
        result = prime * result + ((tpDst == null) ? 0 : tpDst.hashCode());
        result = prime * result + ((tpSrc == null) ? 0 : tpSrc.hashCode());
        result = prime * result + ((vlanId == null) ? 0 : vlanId.hashCode());
        result = prime * result + ((vlanPriority == null) ? 0 : vlanPriority.hashCode());
        result = prime * result + ((idleTimeout == null) ? 0 : idleTimeout.hashCode());
        result = prime * result + ((hardTimeout == null) ? 0 : hardTimeout.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FlowConfig other = (FlowConfig) obj;
        if (actions == null) {
            if (other.actions != null) {
                return false;
            }
        } else if (!actions.equals(other.actions)) {
            return false;
        }
        if (cookie == null) {
            if (other.cookie != null) {
                return false;
            }
        } else if (!cookie.equals(other.cookie)) {
            return false;
        }
        if (dlDst == null) {
            if (other.dlDst != null) {
                return false;
            }
        } else if (!dlDst.equals(other.dlDst)) {
            return false;
        }
        if (dlSrc == null) {
            if (other.dlSrc != null) {
                return false;
            }
        } else if (!dlSrc.equals(other.dlSrc)) {
            return false;
        }
        if (dynamic != other.dynamic) {
            return false;
        }
        if (etherType == null) {
            if (other.etherType != null) {
                return false;
            }
        } else if (!etherType.equals(other.etherType)) {
            return false;
        }
        if (ingressPort == null) {
            if (other.ingressPort != null) {
                return false;
            }
        } else if (!ingressPort.equals(other.ingressPort)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (nwDst == null) {
            if (other.nwDst != null) {
                return false;
            }
        } else if (!nwDst.equals(other.nwDst)) {
            return false;
        }
        if (nwSrc == null) {
            if (other.nwSrc != null) {
                return false;
            }
        } else if (!nwSrc.equals(other.nwSrc)) {
            return false;
        }
        if (portGroup == null) {
            if (other.portGroup != null) {
                return false;
            }
        } else if (!portGroup.equals(other.portGroup)) {
            return false;
        }
        if (priority == null) {
            if (other.priority != null) {
                return false;
            }
        } else if (!priority.equals(other.priority)) {
            return false;
        }
        if (protocol == null) {
            if (other.protocol != null) {
                return false;
            }
        } else if (!protocol.equals(other.protocol)) {
            return false;
        }
        if (node == null) {
            if (other.node != null) {
                return false;
            }
        } else if (!node.equals(other.node)) {
            return false;
        }
        if (tosBits == null) {
            if (other.tosBits != null) {
                return false;
            }
        } else if (!tosBits.equals(other.tosBits)) {
            return false;
        }
        if (tpDst == null) {
            if (other.tpDst != null) {
                return false;
            }
        } else if (!tpDst.equals(other.tpDst)) {
            return false;
        }
        if (tpSrc == null) {
            if (other.tpSrc != null) {
                return false;
            }
        } else if (!tpSrc.equals(other.tpSrc)) {
            return false;
        }
        if (vlanId == null) {
            if (other.vlanId != null) {
                return false;
            }
        } else if (!vlanId.equals(other.vlanId)) {
            return false;
        }
        if (vlanPriority == null) {
            if (other.vlanPriority != null) {
                return false;
            }
        } else if (!vlanPriority.equals(other.vlanPriority)) {
            return false;
        }
        if (idleTimeout == null) {
            if (other.idleTimeout != null) {
                return false;
            }
        } else if (!idleTimeout.equals(other.idleTimeout)) {
            return false;
        }
        if (hardTimeout == null) {
            if (other.hardTimeout != null) {
                return false;
            }
        } else if (!hardTimeout.equals(other.hardTimeout)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "FlowConfig [dynamic=" + dynamic + ", status=" + status + ", installInHw=" + installInHw + ", name="
                + name + ", switchId=" + node + ", ingressPort=" + ingressPort + ", portGroup=" + portGroup
                + ", etherType=" + etherType + ", priority=" + priority + ", vlanId=" + vlanId + ", vlanPriority="
                + vlanPriority + ", dlSrc=" + dlSrc + ", dlDst=" + dlDst + ", nwSrc=" + nwSrc + ", nwDst=" + nwDst
                + ", protocol=" + protocol + ", tosBits=" + tosBits + ", tpSrc=" + tpSrc + ", tpDst=" + tpDst
                + ", cookie=" + cookie + ", idleTimeout=" + idleTimeout + ", hardTimeout=" + hardTimeout + ", actions="
                + actions + "]";
    }
}

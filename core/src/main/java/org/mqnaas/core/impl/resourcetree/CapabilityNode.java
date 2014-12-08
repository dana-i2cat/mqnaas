package org.mqnaas.core.impl.resourcetree;

import org.mqnaas.core.impl.ApplicationInstance;
import org.mqnaas.core.impl.CapabilityInstance;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class CapabilityNode extends ApplicationNode {

	private ResourceNode	parent;

	public CapabilityNode() {
		super();
	}

	public CapabilityNode(CapabilityInstance ci) {
		super(ci);
	}

	public CapabilityNode(CapabilityInstance ci, ResourceNode parent) {
		super(ci);
		this.parent = parent;
	}

	/**
	 * @return the content
	 */
	@Override
	public CapabilityInstance getContent() {
		return (CapabilityInstance) super.getContent();
	}

	/**
	 * @param content
	 *            the content to set
	 * @throws IllegalArgumentException
	 *             if given content is not a CapabilityInstance
	 */
	@Override
	public void setContent(ApplicationInstance content) {
		if (!(content instanceof CapabilityInstance))
			throw new IllegalArgumentException("Expected CapabilityInstance");

		super.setContent(content);
	}

	/**
	 * @return the parent
	 */
	public ResourceNode getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(ResourceNode parent) {
		this.parent = parent;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append(" [");
		
//		sb.append("parent=").append(parent);
		sb.append(getContent());
		
		sb.append("]");
		return sb.toString();
	}
}

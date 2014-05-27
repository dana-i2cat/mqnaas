package org.mqnaas.core.impl.resourcetree;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.impl.CapabilityInstance;

public class CapabilityNode {

	private CapabilityInstance	content;

	private ResourceNode		parent;
	private List<ResourceNode>	children;

	public CapabilityNode() {
		children = new CopyOnWriteArrayList<ResourceNode>();
	}

	public CapabilityNode(CapabilityInstance ci) {
		this();
		content = ci;
	}

	public CapabilityNode(CapabilityInstance ci, ResourceNode parent) {
		this();
		content = ci;
		this.parent = parent;
	}

	/**
	 * @return the content
	 */
	public CapabilityInstance getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(CapabilityInstance content) {
		this.content = content;
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

	/**
	 * @return the children
	 */
	public List<ResourceNode> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<ResourceNode> children) {
		this.children = children;
	}
}

package org.mqnaas.core.impl.resourcetree;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;

public class ResourceNode {

	private IResource				content;

	private CapabilityNode			parent;
	private List<CapabilityNode>	children;

	public ResourceNode() {
		children = new CopyOnWriteArrayList<CapabilityNode>();
	}

	public ResourceNode(IResource resource) {
		this();
		content = resource;
	}

	public ResourceNode(IResource resource, CapabilityNode parent) {
		this();
		content = resource;
		this.parent = parent;
	}

	/**
	 * @return the content
	 */
	public IResource getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(IResource content) {
		this.content = content;
	}

	/**
	 * @return the parent
	 */
	public CapabilityNode getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(CapabilityNode parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	public List<CapabilityNode> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<CapabilityNode> children) {
		this.children = children;
	}

}

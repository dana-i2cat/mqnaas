package org.mqnaas.core.impl.resourcetree;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.impl.ApplicationInstance;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class ApplicationNode {

	private ApplicationInstance	content;

	private List<ResourceNode>	children;

	public ApplicationNode() {
		children = new CopyOnWriteArrayList<ResourceNode>();
	}

	public ApplicationNode(ApplicationInstance ci) {
		this();
		content = ci;
	}

	/**
	 * @return the content
	 */
	public ApplicationInstance getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(ApplicationInstance content) {
		this.content = content;
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

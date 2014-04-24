/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mqnaas.core.impl.bundletree;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Represents a node in a {@link org.apache.karaf.shell.dev.util.Tree}
 */
public class Node<T> {

	private final T			value;
	private Node<T>			parent;
	private List<Node<T>>	children	= new LinkedList<Node<T>>();

	/**
	 * Creates a new node. Only meant for internal use, new nodes should be added using the {@link #addChild(Object)} method
	 * 
	 * @param value
	 *            the node value
	 */
	protected Node(T value) {
		super();
		this.value = value;
	}

	/**
	 * Creates a new node. Only meant for internal use, new nodes should be added using the {@link #addChild(Object)} method
	 * 
	 * @param value
	 *            the node value
	 */
	protected Node(T value, Node<T> parent) {
		this(value);
		this.parent = parent;
	}

	/**
	 * Access the node's value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Access the node's child nodes
	 */
	public List<Node<T>> getChildren() {
		return children;
	}

	/**
	 * Adds a child to this node
	 * 
	 * @param value
	 *            the child's value
	 * @return the child node
	 */
	public Node<T> addChild(T value) {
		Node<T> node = new Node<T>(value, this);
		children.add(node);
		return node;
	}

	/**
	 * Give a set of values in the tree.
	 * 
	 * @return
	 */
	public Set<T> flatten() {
		Set<T> result = new HashSet<T>();
		result.add(getValue());
		for (Node<T> child : getChildren()) {
			result.addAll(child.flatten());
		}
		return result;
	}

	/**
	 * Check if the node has an ancestor that represents the given value
	 * 
	 * @param value
	 *            the node value
	 * @return <code>true</code> it there's an ancestor that represents the value
	 */
	public boolean hasAncestor(T value) {
		if (parent == null) {
			return false;
		} else {
			return value.equals(parent.value) || parent.hasAncestor(value);
		}
	}

}

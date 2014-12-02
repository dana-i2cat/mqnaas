package org.mqnaas.network.impl.topology.link;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;
import org.mqnaas.network.api.topology.link.ILinkManagement;
import org.mqnaas.network.impl.topology.TopologyResource;

/**
 * Implementation of the {@link ILinkManagement} capability backed by a
 * {@link CopyOnWriteArrayList}, which is bound to a {@link TopologyResource}.
 * 
 * @author Georg Mansky-Kummert
 *
 */
public class LinkManagement implements ILinkManagement {

	public static boolean isSupporting(IResource resource) {
		return resource instanceof TopologyResource;
	}

	private List<LinkResource> links;

	@Override
	public IResource createLink() {
		LinkResource link = new LinkResource();
		links.add(link);
		return link;
	}

	@Override
	public void removeLink(IResource link) {
		links.remove(link);
	}

	@Override
	public List<IResource> getLinks() {
		return new ArrayList<IResource>(links);
	}

	@Override
	public void activate() {
		links = new CopyOnWriteArrayList<LinkResource>();
	}

	@Override
	public void deactivate() {
	}

}

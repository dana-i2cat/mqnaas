package org.mqnaas.network.impl.topology.link;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.network.api.topology.link.ILinkManagement;
import org.mqnaas.network.impl.RequestResource;

/**
 * Implementation of the {@link ILinkManagement} capability backed by a {@link CopyOnWriteArrayList}, which is bound to all {@link IRootResource}s as
 * well as to {@link RequestResource}.
 * 
 * @author Georg Mansky-Kummert
 *
 */
public class LinkManagement implements ILinkManagement {

	public static boolean isSupporting(IResource resource) {
		return resource instanceof RequestResource;
	}

	public static boolean isSupporting(IRootResource resource) {
		return (!resource.getDescriptor().getSpecification().getType().equals(Type.CORE));
	}

	private List<LinkResource>	links;

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

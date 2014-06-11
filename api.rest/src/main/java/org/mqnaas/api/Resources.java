package org.mqnaas.api;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Resources {
	public List<String>	resourceId	= new ArrayList<String>();
}

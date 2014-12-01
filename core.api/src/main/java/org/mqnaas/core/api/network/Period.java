package org.mqnaas.core.api.network;

import java.util.Date;

public class Period {
	
	private Date start, end;

	public Date getStart() {
		return start;
	}
	
	public Date getEnd() {
		return end;
	}
	
	public void setEnd(Date end) {
		this.end = end;
	}
	
	public void setStart(Date start) {
		this.start = start;
	}
	
}

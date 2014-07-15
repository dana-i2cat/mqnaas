package org.mqnaas.core.impl.dummy;

import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IService;

public class DummyExecutionService implements IExecutionService {

	@Override
	public void activate() {
	}

	@Override
	public void deactivate() {
	}

	@Override
	public Object execute(IService service, Object[] parameters) {
		// do nothing
		return null;
	}

}

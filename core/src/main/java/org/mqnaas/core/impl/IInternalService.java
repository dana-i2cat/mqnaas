package org.mqnaas.core.impl;

import org.mqnaas.core.api.IService;

public interface IInternalService extends IService {

	public Object execute(Object[] parameters);

}

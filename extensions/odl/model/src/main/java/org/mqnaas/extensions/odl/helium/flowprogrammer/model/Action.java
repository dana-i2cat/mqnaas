package org.mqnaas.extensions.odl.helium.flowprogrammer.model;

/*
 * #%L
 * MQNaaS :: ODL Model
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i
 * 			Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
public class Action {

	private ActionType	type;
	private String		parameter;

	public Action(ActionType type, String parameter) {
		super();

		this.type = type;
		this.parameter = parameter;

		checkValidity();
	}

	public Action(String type, String parameter) {
		if (type == null)
			throw new IllegalArgumentException("Type cannot be null");

		ActionType validType = null;
		for (ActionType candidate : ActionType.values()) {
			if (candidate.toString().equals(type)) {
				validType = candidate;
				break;
			}
		}
		if (validType == null)
			throw new IllegalArgumentException("Invalid action type");

		this.type = validType;
		this.parameter = parameter;

		checkValidity();
	}

	public Action(ActionType type) {
		this(type, null);
	}

	// To satisfy JAXB
	@SuppressWarnings("unused")
	private Action() {

	}

	public ActionType getType() {
		return type;
	}

	public String getParameter() {
		return parameter;
	}

	@Override
	public String toString() {
		// Returned value is being used in org.mqnaas.extensions.odl.client.api.model.FlowConfig
		// it will be parsed by ODL in org.opendaylight.controller.forwardingrulesmanager.FlowConfig
		if (!type.takesParameter())
			return type.toString();
		return type.toString() + "=" + parameter;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((parameter == null) ? 0 : parameter.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Action other = (Action) obj;
		if (parameter == null) {
			if (other.parameter != null)
				return false;
		} else if (!parameter.equals(other.parameter))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	private void checkValidity() {
		if (type == null)
			throw new IllegalArgumentException("Type cannot be null");

		if (type.takesParameter())
			if (parameter == null)
				throw new IllegalArgumentException("Parameter is required for action " + type.toString());
	}
}

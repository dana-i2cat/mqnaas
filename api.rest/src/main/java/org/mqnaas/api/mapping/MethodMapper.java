package org.mqnaas.api.mapping;

/*
 * #%L
 * MQNaaS :: REST API Provider
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.mqnaas.api.translators.Translator;

public class MethodMapper {

	private Method			method;

	private Translator[]	parametersTranslators;
	private Translator		resultTranslator;

	private Object			capabilityInstance;

	public MethodMapper(Method method, Object capabilityInstance) {
		this.method = method;
		this.capabilityInstance = capabilityInstance;
	}

	public void setResultTranslator(Translator resultTranslator) {
		this.resultTranslator = resultTranslator;
	}

	public void setParametersTranslators(Translator[] parametersTranslators) {
		this.parametersTranslators = parametersTranslators;
	}

	public Object invoke(Object target, Object... params) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				Translator translator = parametersTranslators != null ? parametersTranslators[i] : null;

				if (translator != null && params[i] != null) {
					params[i] = translator.translate(params[i]);
				}
			}
		}

		Object result = method.invoke(capabilityInstance, params);

		return resultTranslator != null && result != null ? resultTranslator.translate(result) : result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("maps to ");

		sb.append(method.getReturnType().getSimpleName()).append(" ");

		sb.append(method.getName());
		sb.append("(");

		int i = 0;
		for (Class<?> parameterType : method.getParameterTypes()) {
			if (i > 0)
				sb.append(", ");
			sb.append(parameterType.getSimpleName());
			i++;
		}

		sb.append(")");

		return sb.toString();
	}

}
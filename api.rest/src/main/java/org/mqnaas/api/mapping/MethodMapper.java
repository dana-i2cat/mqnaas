package org.mqnaas.api.mapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodMapper {

	public interface Translator {
		Object translate(Object input);
	}

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

	// public Translator getResultTranslator() {
	// return resultTranslator;
	// }

	public void setParametersTranslators(Translator[] parametersTranslators) {
		this.parametersTranslators = parametersTranslators;
	}

	// public Translator[] getParametersTranslators() {
	// return parametersTranslators;
	// }

	// public Method getMethod() {
	// return method;
	// }

	public Object invoke(Object target, Object... params) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		for (int i = 0; i < params.length; i++) {
			Translator translator = parametersTranslators != null ? parametersTranslators[i] : null;

			if (translator != null && params[i] != null) {
				params[i] = translator.translate(params[i]);
			}
		}

		Object result = method.invoke(capabilityInstance, params);

		return resultTranslator != null ? resultTranslator.translate(result) : result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("maps to ");

		sb.append(method.getReturnType().getName()).append(" ");

		sb.append(method.getName());
		sb.append("(");

		int i = 0;
		for (Class<?> parameterType : method.getParameterTypes()) {
			if (i > 0)
				sb.append(", ");
			sb.append(parameterType.getName());
			i++;
		}

		sb.append(")");

		return sb.toString();
	}

}
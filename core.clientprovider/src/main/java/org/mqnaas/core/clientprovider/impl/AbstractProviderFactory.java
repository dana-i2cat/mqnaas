package org.mqnaas.core.clientprovider.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

public class AbstractProviderFactory {

	protected static boolean doTypeArgumentsMatch(Set<Type> validTypes, Class<?> clazz1, Class<?> class2, int numArgs) {

		for (int i = 0; i < numArgs; i++) {
			if (!getTypeArgument(validTypes, i, clazz1).equals(getTypeArgument(validTypes, i, class2))) {
				return false;
			}
		}

		return true;
	}

	private static Type getTypeArgument(Set<Type> validTypes, int index, Class<?> clientProviderClass) {

		// Look for the specific generic interfaces...
		for (Type type : clientProviderClass.getGenericInterfaces()) {

			ParameterizedType parameterizedType = (ParameterizedType) type;

			if (validTypes.contains(parameterizedType.getRawType())) {
				return parameterizedType.getActualTypeArguments()[index];
			}
		}

		return null;
	}

}

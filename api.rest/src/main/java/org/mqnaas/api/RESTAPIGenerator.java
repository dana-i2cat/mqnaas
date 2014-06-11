package org.mqnaas.api;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.mqnaas.api.mapping.APIMapper;
import org.mqnaas.api.mapping.MethodMapper;
import org.mqnaas.api.mapping.MethodMapper.Translator;
import org.mqnaas.api.writers.InterfaceWriter;
import org.mqnaas.api.writers.MethodWriter;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IIdentifiable;
import org.objectweb.asm.Opcodes;

public class RESTAPIGenerator implements Opcodes {

	/**
	 * Creates the API interface for a capability alongside the mappers necessary for parameters and return values.
	 * 
	 * @param interfaceWriter
	 * 
	 * @param javaAPI
	 * @param rootResourceManagement
	 * @return
	 * @throws Exception
	 */
	public static APIMapper createAPIInterface(Class<?> apiInterface, InterfaceWriter interfaceWriter, final Class<? extends ICapability> javaAPI,
			final ICapability javaInstance)
			throws Exception {

		APIMapper mapper = new APIMapper(apiInterface, javaAPI);

		// Map<String, Method> mapping = new HashMap<String, Method>();
		//
		// for (Method method : javaAPI.getDeclaredMethods()) {
		// mapping.put(method.getName(), method);
		// }

		// A specific translator that should/could be using a cache to make the translation from an id to its identifiable
		// translators.put(new ImmutablePair<Class<?>, Class<?>>(String.class, IRootResource.class), new Translator() {
		//
		// @Override
		// public IRootResource translate(Object input) {
		// String id = (String) input;
		//
		// IRootResourceManagement rootResourceManagement = (IRootResourceManagement) javaInstance;
		//
		// for (IRootResource resource : rootResourceManagement.getRootResources()) {
		// if (resource.getId().equals(id)) {
		// return resource;
		// }
		// }
		//
		// throw new ResourceNotFoundException("Root resource with ID " + id + " does not exist.");
		// }
		//
		// @Override
		// public String toString() {
		// return "RootResourceManagement specific lookup translator";
		// }
		//
		// });

		for (Entry<Method, MethodWriter> mapping : interfaceWriter.getMapping()) {

			Method methodJava = mapping.getKey();

			MethodWriter writer = mapping.getValue();

			Method methodREST = apiInterface.getDeclaredMethod(writer.getName(), writer.getParameterTypes());

			System.out.println("----------------------------------------------------------------\nM e t h o d  " + methodREST.getName());

			MethodMapper mm;

			if (methodJava == null) {

				// Generic getter
				GenericGetter gg = new GenericGetter(javaInstance, interfaceWriter.getListService());

				mm = new MethodMapper(gg.getIdResolverMethod(), gg);

				// StringBuilder sb = new StringBuilder();
				//
				// sb.append(methodREST.getName()).append("(");
				// StringBuilderUtils.append(sb, StringBuilderUtils.CLASSNAME_EXTRACTOR, methodREST.getParameterTypes());
				// sb.append(")");
				//
				// System.out.println("Skipping unmapped method: " + sb.toString());
				// continue;
			} else {
				mm = new MethodMapper(methodJava, javaInstance);

				Class<?>[] parameterTypesREST = methodREST.getParameterTypes();
				Class<?>[] parameterTypesJava = methodJava.getParameterTypes();

				if (parameterTypesREST.length != parameterTypesJava.length) {
					throw new RuntimeException("Can this happen, assure that it can't when translating the methods");
				}

				Translator[] parametersTranslators = new Translator[parameterTypesREST.length];

				for (int i = 0; i < parameterTypesJava.length; i++) {
					parametersTranslators[i] = getTranslator(parameterTypesREST[i], parameterTypesJava[i]);
				}

				mm.setParametersTranslators(parametersTranslators);

				mm.setResultTranslator(getTranslator(methodJava.getReturnType(), methodREST.getReturnType()));
			}

			mapper.addMethodMapper(methodREST, mm);

		}

		return mapper;
	}

	public static class GenericGetter {

		private ICapability	capability;

		private Method		listService;

		GenericGetter(ICapability capability, Method listService) throws InvalidCapabilityDefinionException {
			this.capability = capability;
			this.listService = listService;
		}

		public Method getIdResolverMethod() {
			try {
				return getClass().getMethod("map", String.class);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public Object map(String id) {
			try {
				Collection<?> resources = (Collection<?>) listService.invoke(capability, new Object[0]);

				for (Object resource : resources) {
					IIdentifiable identifiable = (IIdentifiable) resource;

					if (identifiable.getId().equals(id))
						return identifiable;
				}

				return null;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}
	}

	private static Map<Pair<Class<?>, Class<?>>, Translator>	translators	= new HashMap<Pair<Class<?>, Class<?>>, Translator>();

	static {
		// A generic translator that can be used to translate IIdentifiables to Strings
		translators.put(new ImmutablePair<Class<?>, Class<?>>(IIdentifiable.class, String.class), new Translator() {

			@Override
			public Object translate(Object input) {
				IIdentifiable identifiable = (IIdentifiable) input;
				return identifiable.getId();
			}

			@Override
			public String toString() {
				return "Default Identifieable to ID translator";
			}
		});

	}

	private static Translator getTranslator(Class<?> inputClass, Class<?> outputClass) {

		Translator translator = null;

		if (!inputClass.isAssignableFrom(outputClass)) {
			for (Pair<Class<?>, Class<?>> transition : translators.keySet()) {

				Class<?> left = transition.getLeft();
				Class<?> right = transition.getRight();

				if (left.isAssignableFrom(inputClass) && outputClass.isAssignableFrom(right)) {
					translator = translators.get(transition);
					break;
				}
			}
		}

		return translator;
	}

	public static Class<?> getTranslation(Class<?> clazz) {

		for (Pair<Class<?>, Class<?>> translation : translators.keySet()) {

			Class<?> left = translation.getLeft();
			Class<?> right = translation.getRight();

			if (left.isAssignableFrom(clazz)) {
				return right;
			}
		}

		return null;
	}

}

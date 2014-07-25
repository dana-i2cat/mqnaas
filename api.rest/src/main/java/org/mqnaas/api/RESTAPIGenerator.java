package org.mqnaas.api;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map.Entry;

import org.mqnaas.api.exceptions.InvalidCapabilityDefinionException;
import org.mqnaas.api.mapping.APIMapper;
import org.mqnaas.api.mapping.MethodMapper;
import org.mqnaas.api.translators.ResourceTranslator.ResourceResolver;
import org.mqnaas.api.translators.Translator;
import org.mqnaas.api.translators.Translators;
import org.mqnaas.api.writers.InterfaceWriter;
import org.mqnaas.api.writers.MethodWriter;
import org.mqnaas.core.api.ICapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RESTAPIGenerator {

	private static final Logger	log			= LoggerFactory.getLogger(RESTAPIGenerator.class);

	private Translators			translators	= new Translators();

	/**
	 * Creates the API interface for a capability alongside the mappers necessary for parameters and return values.
	 * 
	 * @author Georg Mansky-Kummert (i2CAT)
	 * 
	 * @param interfaceWriter
	 * 
	 * @param javaAPI
	 * @param rootResourceManagement
	 * @return
	 * @throws InvalidCapabilityDefinionException
	 *             Is thrown, when no list method in the capability is found to support the
	 */
	public APIMapper createAPIInterface(Class<?> apiInterface, InterfaceWriter interfaceWriter, final Class<? extends ICapability> javaAPI,
			ICapability javaInstance) throws InvalidCapabilityDefinionException
	{

		APIMapper mapper = new APIMapper(apiInterface, javaAPI);

		ResourceResolver resourceResolver = new ResourceResolver(javaInstance, interfaceWriter.getListService());

		if (interfaceWriter.getListService() != null) {
			// Every generic list service must be available to resource translator
			// TODO Refine, restructure and rethink this process
			translators.addResourceResolver(resourceResolver);
		}

		for (Entry<Method, MethodWriter> mapping : interfaceWriter.getMapping()) {

			Method methodJava = mapping.getKey();
			MethodWriter writer = mapping.getValue();

			Method methodREST;
			try {
				methodREST = apiInterface.getMethod(writer.getName(), writer.getParameterTypes());
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				String message = "Expected generated API to contain method " + writer.getName() + "(" + Arrays.toString(writer.getParameterTypes()) + ")";
				throw new RuntimeException(message, e);
			}

			MethodMapper mm;

			if (methodJava == null) {
				if (resourceResolver != null) {
					mm = new MethodMapper(resourceResolver.getResolvementMethod(), resourceResolver);
				} else {
					throw new InvalidCapabilityDefinionException(
							"No list method available on capability " + javaAPI.getName() + ". Mapping of " + methodREST + " not possible.");
				}

			} else {
				mm = new MethodMapper(methodJava, javaInstance);

				Class<?>[] parameterTypesREST = methodREST.getParameterTypes();
				Class<?>[] parameterTypesJava = methodJava.getParameterTypes();

				if (parameterTypesREST.length != parameterTypesJava.length) {
					throw new RuntimeException("Can this happen?. Assure that it can't when translating the methods");
				}

				Translator[] parametersTranslators = new Translator[parameterTypesREST.length];

				for (int i = 0; i < parameterTypesJava.length; i++) {
					parametersTranslators[i] = translators.getTranslator(parameterTypesREST[i], parameterTypesJava[i]);
				}

				mm.setParametersTranslators(parametersTranslators);

				mm.setResultTranslator(translators.getTranslator(methodJava.getReturnType(), methodREST.getReturnType()));
			}

			log.info("Adding MethodMapper for {}: {}", methodREST, mm);

			mapper.addMethodMapper(methodREST, mm);

		}

		return mapper;
	}

	public Class<?> getTranslation(Class<?> clazz) {
		return translators.getTranslation(clazz);
	}
}

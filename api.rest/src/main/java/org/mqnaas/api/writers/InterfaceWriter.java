package org.mqnaas.api.writers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.i2cat.utils.StringBuilderUtils;
import org.mqnaas.api.ContentType;
import org.mqnaas.api.InvalidCapabilityDefinionException;
import org.mqnaas.api.RESTAPIGenerator;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IIdentifiable;
import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.core.api.annotations.ListResources;
import org.mqnaas.core.api.annotations.RemovesResource;
import org.objectweb.asm.ClassWriter;

public class InterfaceWriter extends AbstractWriter {

	private String						name;
	private AnnotationWriter[]			annotationWriters;

	private List<MethodWriter>			methodWriters;
	private Map<Method, MethodWriter>	method2writer;

	private Method						listService;

	public InterfaceWriter(Class<? extends ICapability> capabilityClass, String endpoint) throws Exception {

		name = generateAPIInterfaceName(capabilityClass);
		annotationWriters = new AnnotationWriter[] { new AnnotationWriter(Path.class, new AnnotationParamWriter("value",
				endpoint)) };

		methodWriters = new ArrayList<MethodWriter>();

		// All methods defined in the capability are services.
		List<Method> services = new ArrayList<Method>(Arrays.asList(capabilityClass.getDeclaredMethods()));

		List<Method> resourceAddingServices = filter(services, AddsResource.class);
		List<Method> resourceDeletingServices = filter(services, RemovesResource.class);
		List<Method> resourceListingServices = filter(services, ListResources.class);

		// CHECK THE BASIC CONTRACT
		for (Method m : resourceAddingServices) {
			// Either has to returns IIdentifiable or to define a single IIdentifiable parameters
			boolean returnsIdentifiable = IIdentifiable.class.isAssignableFrom(m.getReturnType());
			boolean hasIdentifiableParameter = m.getParameterTypes().length == 1 && IIdentifiable.class.isAssignableFrom(m.getParameterTypes()[0]);

			if (!returnsIdentifiable && !hasIdentifiableParameter) {
				throw new InvalidCapabilityDefinionException(
						"Creation service " + m.getName() + " has to either: (a) return a subclass of " + IIdentifiable.class + " or (b) has a single parameter subclassing " + IIdentifiable.class);
			}
		}

		for (Method m : resourceDeletingServices) {
			// Have to have one parameter subclassing IIdentifiable
			if (m.getParameterTypes().length != 1) {
				throw new InvalidCapabilityDefinionException(
						"Deletion service " + m.getName() + " has to have exactly one parameter extending " + IIdentifiable.class);
			} else if (!IIdentifiable.class.isAssignableFrom(m.getParameterTypes()[0])) {
				throw new InvalidCapabilityDefinionException(
						"Deletion service " + m.getName() + ": Parameter must be a subclass of " + IIdentifiable.class);
			}
		}

		for (Method m : resourceListingServices) {
			// Has to return a collection of IIdentifiables
			if (!Collection.class.isAssignableFrom(m.getReturnType())) {
				throw new InvalidCapabilityDefinionException("List service " + m.getName() + " has to return a collection of " + IIdentifiable.class);
			}

			Class<?> returnParameterType = getReturnTypeParameter(m);

			if (!IIdentifiable.class.isAssignableFrom(returnParameterType)) {
				throw new InvalidCapabilityDefinionException(
						"List service " + m.getName() + " has to return collections of " + IIdentifiable.class);
			}
		}

		// COLLECT THE ENTITY TYPES USED
		Set<Class<?>> entityClasses = new HashSet<Class<?>>();

		for (Method m : resourceAddingServices) {
			if (IIdentifiable.class.isAssignableFrom(m.getReturnType())) {
				entityClasses.add(m.getReturnType());
			} else {
				entityClasses.add(m.getParameterTypes()[0]);
			}
		}

		for (Method m : resourceDeletingServices) {
			entityClasses.add(m.getParameterTypes()[0]);
		}

		for (Method m : resourceListingServices) {
			entityClasses.add(getReturnTypeParameter(m));
		}

		if (entityClasses.size() != 1) {
			throw new InvalidCapabilityDefinionException("More than one entity class found: " + entityClasses);
		}

		// INITIALIZE THE LIST SERVICE
		for (Method m : resourceListingServices) {
			if (m.getParameterTypes().length == 0) {
				listService = m;
				break;
			}
		}

		if (listService == null && !resourceListingServices.isEmpty()) {
			throw new InvalidCapabilityDefinionException("No list service without parameters found.");
		}

		Class<?> entityClass = entityClasses.iterator().next();

		method2writer = new HashMap<Method, MethodWriter>();

		for (Method m : resourceAddingServices) {

			Class<?> resultClass = RESTAPIGenerator.getTranslation(m.getReturnType());

			MethodWriter writer = new MethodWriter(m.getName(), resultClass, m.getParameterTypes(),
					new AnnotationWriter(PUT.class),
					new AnnotationWriter(Consumes.class, new AnnotationParamWriter("value", new String[] { "application/xml" })));

			method2writer.put(m, writer);
		}

		for (Method m : resourceDeletingServices) {

			Class<?> parameterClass = RESTAPIGenerator.getTranslation(m.getParameterTypes()[0]);

			MethodWriter writer = new MethodWriter(m.getName(), void.class, new Class<?>[] { parameterClass },
					new AnnotationWriter(DELETE.class),
					new AnnotationWriter(Path.class, new AnnotationParamWriter("value", "{id}")),
					new AnnotationWriter(0, PathParam.class, new AnnotationParamWriter("value", "id")));

			method2writer.put(m, writer);
		}

		for (Method m : resourceListingServices) {

			MethodWriter writer = new MethodWriter(m.getName(), m.getReturnType(),
					m.getParameterTypes(),
					new AnnotationWriter(GET.class),
					new AnnotationWriter(ContentType.class, new AnnotationParamWriter("value", entityClass)),
					new AnnotationWriter(Produces.class, new AnnotationParamWriter("value", new String[] { "application/xml" })));

			// Map all parameters as QueryParams

			String[] names = null; // TODO read names using asm

			for (int i = 0; i < m.getParameterTypes().length; i++) {
				String name = names != null ? names[i] : "arg" + i;
				writer.addAnnotationWriter(new AnnotationWriter(i, QueryParam.class, new AnnotationParamWriter("value", name)));
			}

			method2writer.put(m, writer);
		}

		services.removeAll(resourceAddingServices);
		services.removeAll(resourceDeletingServices);
		services.removeAll(resourceListingServices);

		for (Method m : services) {

			MethodWriter writer = new MethodWriter(m.getName(), m.getReturnType(), m.getParameterTypes(),
					new AnnotationWriter(GET.class),
					new AnnotationWriter(Path.class, new AnnotationParamWriter("value", m.getName())));

			String[] names = null; // TODO read names using asm

			for (int i = 0; i < m.getParameterTypes().length; i++) {
				String name = names != null ? names[i] : "arg" + i;
				writer.addAnnotationWriter(new AnnotationWriter(i, QueryParam.class, new AnnotationParamWriter("value", name)));
			}

			method2writer.put(m, writer);
		}

		methodWriters.addAll(method2writer.values());

		// Resource Getter with id
		MethodWriter methodWriter = new MethodWriter("get" + entityClass.getSimpleName(), entityClass, new Class<?>[] { String.class },
				new AnnotationWriter(GET.class),
				new AnnotationWriter(Path.class, new AnnotationParamWriter("value", "{id}")),
				new AnnotationWriter(Produces.class, new AnnotationParamWriter("value", new String[] { "application/xml" })),
				new AnnotationWriter(0, PathParam.class, new AnnotationParamWriter("value", "id")));

		method2writer.put(null, methodWriter);

		methodWriters.add(methodWriter);

	}

	private Class<?> getReturnTypeParameter(Method m) {

		Class<?> clazz = null;

		Type returnType = m.getGenericReturnType();
		if (returnType instanceof ParameterizedType) {
			ParameterizedType collectionType = (ParameterizedType) returnType;

			Type type = collectionType.getActualTypeArguments()[0];
			if (type instanceof Class<?>) {
				clazz = (Class<?>) type;
			}
		}

		return clazz;
	}

	private List<Method> filter(List<Method> services, Class<? extends Annotation> annotationClass) {

		List<Method> filtered = new ArrayList<Method>();

		for (Method service : services) {
			if (service.getAnnotation(annotationClass) != null) {
				filtered.add(service);
			}
		}

		return filtered;
	}

	public Class<?> toClass() {

		String className = name.replace("/", ".");
		byte[] b = write();

		return loadClass(className, b);
	}

	/**
	 * Adds an additional "api" package to the given class' name and returns this new class name
	 * 
	 * @param capabiltyClass
	 * @return
	 */
	private static String generateAPIInterfaceName(Class<? extends ICapability> capabiltyClass) {
		String[] parts = capabiltyClass.getName().split("\\.");

		StringBuilder apiNameBuilder = new StringBuilder();
		for (int i = 0; i < parts.length - 1; i++)
			apiNameBuilder.append(parts[i]).append("/");

		apiNameBuilder.append("api").append("/");
		apiNameBuilder.append(parts[parts.length - 1]);

		return apiNameBuilder.toString();
	}

	public byte[] write() {
		ClassWriter cw = new ClassWriter(0);

		cw.visit(RESTAPIGenerator.V1_6, RESTAPIGenerator.ACC_PUBLIC + RESTAPIGenerator.ACC_ABSTRACT + RESTAPIGenerator.ACC_INTERFACE, name, null,
				"java/lang/Object", null);

		if (annotationWriters != null) {
			for (AnnotationWriter annotation : annotationWriters) {
				annotation.writeTo(cw);
			}
		}

		for (MethodWriter methodWriter : methodWriters) {
			methodWriter.writeTo(cw);
		}

		cw.visitEnd();

		return cw.toByteArray();
	}

	public void addMethodWriter(MethodWriter methodWriter) {
		methodWriters.add(methodWriter);
	}

	@Override
	public String toString() {
		StringBuilder sb = StringBuilderUtils.create("\n", annotationWriters).append("\n");
		sb.append("class ").append(name).append("\n");
		StringBuilderUtils.append(sb, "\n", methodWriters);
		return sb.toString();
	}

	public Set<Entry<Method, MethodWriter>> getMapping() {
		return method2writer.entrySet();
	}

	public Method getListService() {
		return listService;
	}
}
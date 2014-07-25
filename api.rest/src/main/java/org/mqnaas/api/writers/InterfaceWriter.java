package org.mqnaas.api.writers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
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
import org.mqnaas.api.RESTAPIGenerator;
import org.mqnaas.api.exceptions.InvalidCapabilityDefinionException;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.core.api.annotations.ListsResources;
import org.mqnaas.core.api.annotations.RemovesResource;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class InterfaceWriter extends AbstractWriter implements Opcodes {

	private RESTAPIGenerator			restAPIGenerator	= new RESTAPIGenerator();

	private CapabilityMetaDataContainer	metaDataContainer;

	private String						name;

	private AnnotationWriter[]			annotationWriters;

	// private List<MethodWriter> methodWriters;
	private Map<Method, MethodWriter>	method2writer;

	public InterfaceWriter(Class<? extends ICapability> capabilityClass, String endpoint) throws InvalidCapabilityDefinionException {

		// (1) Collect the metadata necessary to write the REST API interface. This process also checks the validity of the given capability.
		metaDataContainer = new CapabilityMetaDataContainer(capabilityClass);

		// (2) Generate a name for the REST API interface
		name = generateAPIInterfaceName(capabilityClass);

		// (3) Initialize the interfaces' path annotation
		// annotationWriters = new AnnotationWriter[] { new AnnotationWriter(Path.class, new AnnotationParamWriter("value", endpoint)) };

		// (4) Define all available services, e.g. methods of the interface
		method2writer = new HashMap<Method, MethodWriter>();

		for (Method m : metaDataContainer.getServiceMethods(AddsResource.class)) {

			Class<?> resultClass = restAPIGenerator.getTranslation(m.getReturnType());

			MethodWriter writer = new MethodWriter(m.getName(), resultClass, m.getParameterTypes(),
					new AnnotationWriter(PUT.class),
					new AnnotationWriter(Consumes.class, new AnnotationParamWriter("value", new String[] { "application/xml" })));

			method2writer.put(m, writer);
		}

		for (Method m : metaDataContainer.getServiceMethods(RemovesResource.class)) {

			Class<?> parameterClass = restAPIGenerator.getTranslation(m.getParameterTypes()[0]);

			MethodWriter writer = new MethodWriter(m.getName(), void.class, new Class<?>[] { parameterClass },
					new AnnotationWriter(DELETE.class),
					new AnnotationWriter(Path.class, new AnnotationParamWriter("value", "{id}")),
					new AnnotationWriter(0, PathParam.class, new AnnotationParamWriter("value", "id")));

			method2writer.put(m, writer);
		}

		for (Method m : metaDataContainer.getServiceMethods(ListsResources.class)) {

			MethodWriter writer = new MethodWriter(m.getName(), m.getReturnType(),
					m.getParameterTypes(),
					new AnnotationWriter(GET.class),
					new AnnotationWriter(ContentType.class, new AnnotationParamWriter("value", metaDataContainer.getEntityClass())),
					new AnnotationWriter(Produces.class, new AnnotationParamWriter("value", new String[] { "application/xml" })));

			// Map all parameters as QueryParams

			String[] names = null; // TODO read names using asm

			for (int i = 0; i < m.getParameterTypes().length; i++) {
				String name = names != null ? names[i] : "arg" + i;
				writer.addAnnotationWriter(new AnnotationWriter(i, QueryParam.class, new AnnotationParamWriter("value", name)));
			}

			method2writer.put(m, writer);
		}

		// Add all the remaining services to the REST API interface
		// 1. Determine all remaining services
		List<Method> methods = metaDataContainer.getServiceMethods();
		methods.removeAll(metaDataContainer.getServiceMethods(AddsResource.class));
		methods.removeAll(metaDataContainer.getServiceMethods(RemovesResource.class));
		methods.removeAll(metaDataContainer.getServiceMethods(ListsResources.class));

		// 2. Write them as services methods with their names as path
		for (Method method : methods) {

			// Define the HTTP method type
			Class<? extends Annotation> httpMethod = GET.class;

			String serviceName = method.getName();
			if (serviceName.startsWith("get") && serviceName.length() > 3) {
				serviceName = serviceName.substring(3, 4).toLowerCase() + serviceName.substring(4);
			}

			// Translate the result
			Class<?> resultClass = restAPIGenerator.getTranslation(method.getReturnType());

			// Translate the parameters
			Class<?>[] parameterClasses = new Class<?>[method.getParameterTypes().length];
			for (int i = 0; i < method.getParameterTypes().length; i++) {
				parameterClasses[i] = restAPIGenerator.getTranslation(method.getParameterTypes()[i]);
			}

			MethodWriter writer = new MethodWriter(method.getName(), resultClass, parameterClasses,
					new AnnotationWriter(httpMethod),
					new AnnotationWriter(Path.class, new AnnotationParamWriter("value", serviceName)));

			String[] names = null; // TODO read names using asm

			for (int i = 0; i < method.getParameterTypes().length; i++) {
				String name = names != null ? names[i] : "arg" + i;
				writer.addAnnotationWriter(new AnnotationWriter(i, QueryParam.class, new AnnotationParamWriter("value", name)));
			}

			method2writer.put(method, writer);
		}

		Class<?> entityClass = metaDataContainer.getEntityClass();
		if (entityClass != null) {
			// If there are entity classes used in the services, then provide a getter for the serialization of the resource
			MethodWriter methodWriter = new MethodWriter("get" + entityClass.getSimpleName(), entityClass, new Class<?>[] { String.class },
					new AnnotationWriter(GET.class),
					new AnnotationWriter(Path.class, new AnnotationParamWriter("value", "{id}")),
					new AnnotationWriter(Produces.class, new AnnotationParamWriter("value", new String[] { "application/xml" })),
					new AnnotationWriter(0, PathParam.class, new AnnotationParamWriter("value", "id")));

			method2writer.put(null, methodWriter);
		}

	}

	public Class<?> toClass(ClassLoader classLoader) {

		String className = name.replace("/", ".");
		byte[] b = write();

		Class<?> clazz = loadClass(classLoader, className, b);

		return clazz;
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

		cw.visit(V1_6, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, name, null,
				"java/lang/Object", null);

		if (annotationWriters != null) {
			for (AnnotationWriter annotation : annotationWriters) {
				annotation.writeTo(cw);
			}
		}

		for (MethodWriter methodWriter : method2writer.values()) {
			methodWriter.writeTo(cw);
		}

		cw.visitEnd();

		return cw.toByteArray();
	}

	public Set<Entry<Method, MethodWriter>> getMapping() {
		return method2writer.entrySet();
	}

	public Method getListService() {
		return metaDataContainer.getListService();
	}

	@Override
	public String toString() {
		StringBuilder sb = StringBuilderUtils.create("\n", annotationWriters).append("\n");
		sb.append("interface ").append(name).append(" {\n");
		StringBuilderUtils.append(sb, "\n\n", method2writer.values());
		sb.append("}");
		return sb.toString();
	}
}
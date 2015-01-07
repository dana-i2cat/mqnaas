package org.mqnaas.api.writers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
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
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;

public class InterfaceWriter extends AbstractWriter implements Opcodes {

	private static final Logger			log					= LoggerFactory.getLogger(InterfaceWriter.class);

	private RESTAPIGenerator			restAPIGenerator	= new RESTAPIGenerator();

	private CapabilityMetaDataContainer	metaDataContainer;

	private String						name;

	private AnnotationWriter[]			annotationWriters;

	// private List<MethodWriter> methodWriters;
	private Map<Method, MethodWriter>	method2writer;

	public InterfaceWriter(Class<? extends ICapability> capabilityClass, String endpoint) throws InvalidCapabilityDefinionException {

		log.debug("Writing REST API interface for class " + capabilityClass + " in endpoint " + endpoint + " .");

		// (1) Collect the metadata necessary to write the REST API interface. This process also checks the validity of the given capability.
		metaDataContainer = new CapabilityMetaDataContainer(capabilityClass);

		// (2) Generate a name for the REST API interface
		name = generateAPIInterfaceName(capabilityClass);

		// (3) Initialize the interfaces' path annotation
		// annotationWriters = new AnnotationWriter[] { new AnnotationWriter(Path.class, new AnnotationParamWriter("value", endpoint)) };

		// (4) Define all available services, e.g. methods of the interface
		method2writer = new HashMap<Method, MethodWriter>();

		for (Method m : metaDataContainer.getServiceMethods(AddsResource.class)) {

			log.trace("Found AddsResource annotated method: " + m);

			// Translate the result
			Class<?> resultClass = restAPIGenerator.getResultTranslation(m.getReturnType());

			// Translate the parameters
			Class<?>[] parameterClasses = new Class<?>[m.getParameterTypes().length];
			for (int i = 0; i < m.getParameterTypes().length; i++) {
				parameterClasses[i] = restAPIGenerator.getParameterTranslation(m.getParameterTypes()[i]);
			}

			MethodWriter writer = new MethodWriter(m.getName(), resultClass, parameterClasses);

			String[] names = null; // TODO read names using asm
			if (m.getParameterTypes().length > 0) {
				// TODO treat multiple parameters annotated with XMLRootElement
				for (int i = 0; i < m.getParameterTypes().length; i++) {
					if (!m.getParameterTypes()[i].isAnnotationPresent(XmlRootElement.class)) {
						String name = names != null ? names[i] : "arg" + i;
						writer.addAnnotationWriter(new AnnotationWriter(i, QueryParam.class, new AnnotationParamWriter("value", name)));
					}
				}
			}

			writer.addAnnotationWriter(new AnnotationWriter(PUT.class));
			writer.addAnnotationWriter(new AnnotationWriter(Consumes.class, new AnnotationParamWriter("value",
					new String[] { MediaType.APPLICATION_XML })));

			method2writer.put(m, writer);
		}

		for (Method m : metaDataContainer.getServiceMethods(RemovesResource.class)) {

			log.trace("Found RemovesResource annotated method: " + m);

			Class<?> parameterClass = restAPIGenerator.getParameterTranslation(m.getParameterTypes()[0]);

			MethodWriter writer = new MethodWriter(m.getName(), void.class, new Class<?>[] { parameterClass },
					new AnnotationWriter(DELETE.class),
					new AnnotationWriter(Path.class, new AnnotationParamWriter("value", "{id}")),
					new AnnotationWriter(0, PathParam.class, new AnnotationParamWriter("value", "id")));

			method2writer.put(m, writer);
		}

		for (Method m : metaDataContainer.getServiceMethods(ListsResources.class)) {

			log.trace("Found ListsResources annotated method: " + m);

			MethodWriter writer = new MethodWriter(m.getName(), m.getReturnType(),
					m.getParameterTypes(),
					new AnnotationWriter(GET.class),
					new AnnotationWriter(ContentType.class, new AnnotationParamWriter("value", metaDataContainer.getEntityClass())),
					new AnnotationWriter(Produces.class, new AnnotationParamWriter("value", new String[] { MediaType.APPLICATION_XML })));

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

			// Translate the result
			Class<?> resultClass = restAPIGenerator.getResultTranslation(method.getReturnType());

			// Translate the parameters
			Class<?>[] parameterClasses = new Class<?>[method.getParameterTypes().length];
			for (int i = 0; i < method.getParameterTypes().length; i++) {
				parameterClasses[i] = restAPIGenerator.getParameterTranslation(method.getParameterTypes()[i]);
			}

			MethodWriter writer = new MethodWriter(method.getName(), resultClass, parameterClasses);

			String serviceName = method.getName();
			if (serviceName.startsWith("get") && serviceName.length() > 3) {
				log.trace("Found \"get\" method: " + method);

				serviceName = serviceName.substring(3, 4).toLowerCase() + serviceName.substring(4);

				if (Collection.class.isAssignableFrom(method.getReturnType())) {
					// FIXME, add wrapper instead of Collection

				}
			}

			else if (serviceName.startsWith("set") && serviceName.length() > 3) {
				log.trace("Found \"set\" method: " + method);

				serviceName = serviceName.substring(3, 4).toLowerCase() + serviceName.substring(4);
				httpMethod = PUT.class;
			}

			writer.addAnnotationWriter(new AnnotationWriter(httpMethod));
			writer.addAnnotationWriter(new AnnotationWriter(Path.class, new AnnotationParamWriter("value", serviceName)));

			String[] names = null; // TODO read names using asm

			// treat one XMLRootElement annotated method parameter (or data structures)
			// FIXME check XMLRootElement is an annotation present in the generic type of Multimap or Collection
			if (method.getParameterTypes().length == 1 && (method.getParameterTypes()[0].isAnnotationPresent(XmlRootElement.class) || Multimap.class
					.isAssignableFrom(method.getParameterTypes()[0]) || Collection.class.isAssignableFrom(method.getParameterTypes()[0]))) {
				if (Multimap.class.isAssignableFrom(method.getParameterTypes()[0])) {
					log.trace("Found Multimap method parameter of type " + method.getParameterTypes()[0]);
					// FIXME, add wrapper instead of Multimap

				} else if (Collection.class.isAssignableFrom(method.getParameterTypes()[0])) {
					log.trace("Found Collection method parameter of type " + method.getParameterTypes()[0]);
					// FIXME, add wrapper instead of Collection

				} else {
					log.trace("Found method parameter annotated with XMLRootElement of type " + method.getParameterTypes()[0]);
					// just do nothing, it will generate a request with a body element
				}
				// add Consumes annotation
				writer.addAnnotationWriter(new AnnotationWriter(Consumes.class, new AnnotationParamWriter("value",
						new String[] { MediaType.APPLICATION_XML })));
			} else {
				// TODO treat parameters annotated with XMLRootElement
				for (int i = 0; i < method.getParameterTypes().length; i++) {
					String name = names != null ? names[i] : "arg" + i;
					writer.addAnnotationWriter(new AnnotationWriter(i, QueryParam.class, new AnnotationParamWriter("value", name)));
				}
			}

			// add Produces if there is a serializable object as the return type
			// FIXME check XMLRootElement is an annotation present in the generic type of Multimap or Collection
			if (resultClass.isAnnotationPresent(XmlRootElement.class)
					|| Multimap.class.isAssignableFrom(resultClass) || Collection.class.isAssignableFrom(resultClass)) {
				log.trace("Found XMLRootElement in the return type class " + resultClass);
				writer.addAnnotationWriter(new AnnotationWriter(Produces.class, new AnnotationParamWriter("value",
						new String[] { MediaType.APPLICATION_XML })));
			}

			method2writer.put(method, writer);
		}

		Class<?> entityClass = metaDataContainer.getEntityClass();
		if (entityClass != null) {
			// If there are entity classes used in the services, then provide a getter for the serialization of the resource
			MethodWriter methodWriter = new MethodWriter("get" + entityClass.getSimpleName(), entityClass, new Class<?>[] { String.class },
					new AnnotationWriter(GET.class),
					new AnnotationWriter(Path.class, new AnnotationParamWriter("value", "{id}")),
					new AnnotationWriter(Produces.class, new AnnotationParamWriter("value", new String[] { MediaType.APPLICATION_XML })),
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
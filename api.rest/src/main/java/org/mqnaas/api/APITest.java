package org.mqnaas.api;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import org.apache.commons.lang.NotImplementedException;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.i2cat.utils.StringBuilderUtils;
import org.mqnaas.api.mapping.APIMapper;
import org.mqnaas.api.mapping.MethodMapper;
import org.mqnaas.api.writers.InterfaceWriter;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IIdentifiable;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;

public class APITest {

	private final class APITestInvocationHandler implements InvocationHandler {

		private APIMapper	mapper;

		public APITestInvocationHandler(APIMapper mapper) {
			this.mapper = mapper;
		}

		@Override
		public Object invoke(Object arg0, Method method, Object[] params) throws Throwable {
			System.out.println("Invoking " + method.getName() + " on " + arg0.getClass() + " with params " + Arrays.toString(params));

			MethodMapper mm = mapper.getMethodMapper(method);

			if (mm == null) {
				StringBuilder sb = StringBuilderUtils.create(params);
				sb.insert(0, "No MethodMapper avaiable for ").append(")");

				// TODO Re-think and re-do behavior in case of failure
				throw new RuntimeException(sb.toString());
			}

			return mm.invoke(arg0, params);
		}
	}

	private JAXRSServerFactoryBean	sf;

	public APITest() throws Exception {

		sf = new JAXRSServerFactoryBean();

		sf.setAddress("http://localhost:9000/");

		sf.setProvider(new MessageBodyWriter<Object>() {

			private Annotation getContentTypeAnnotation(Annotation[] annotations) {
				for (Annotation annotation : annotations) {
					if (annotation.annotationType() == ContentType.class)
						return annotation;
				}

				return null;
			}

			@Override
			public boolean isWriteable(Class<?> type, java.lang.reflect.Type genericType, Annotation[] annotations, MediaType mediaType) {
				return List.class.isAssignableFrom(type) && getContentTypeAnnotation(annotations) != null;
			}

			@Override
			public long getSize(Object t, Class<?> type, java.lang.reflect.Type genericType, Annotation[] annotations, MediaType mediaType) {
				throw new NotImplementedException();
			}

			@Override
			public void writeTo(Object t, Class<?> type, java.lang.reflect.Type genericType, Annotation[] annotations, MediaType mediaType,
					MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

				ContentType contentTypeAnnotation = (ContentType) getContentTypeAnnotation(annotations);

				Class<?> contentType = contentTypeAnnotation.value();

				String name = contentType.getSimpleName();
				String nameId = name + "Id";

				StringBuffer sb = new StringBuffer();

				sb.append("<").append(name).append(">");

				List<?> list = (List<?>) t;

				for (Object value : list) {
					IIdentifiable id = (IIdentifiable) value;

					sb.append("<").append(nameId).append(">");
					sb.append(id.getId());
					sb.append("</").append(nameId).append(">");
				}

				sb.append("</").append(name).append(">");

				entityStream.write(sb.toString().getBytes());
			}
		});

		sf.create();
	}

	public void publish(ICapability capability, Class<? extends ICapability> interfaceToPublish, String uri) throws Exception {

		InterfaceWriter interfaceWriter = new InterfaceWriter(interfaceToPublish, uri);

		Class<?> apiInterface = interfaceWriter.toClass();

		APIMapper mapper = RESTAPIGenerator.createAPIInterface(apiInterface, interfaceWriter, interfaceToPublish, capability);

		sf.setResourceClasses(apiInterface);

		Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { apiInterface }, new APITestInvocationHandler(mapper));

		sf.setResourceProvider(apiInterface, new SingletonResourceProvider(proxy));

		// RootResourceManagement rrm = new RootResourceManagement();
		// rrm.createRootResource(RootResourceDescriptor.create(new Specification(Type.CORE)));
		// rrm.createRootResource(RootResourceDescriptor.create(new Specification(Type.ROUTER, "Junos")));
		// rrm.createRootResource(RootResourceDescriptor.create(new Specification(Type.ROUTER, "Cisco")));
		//
		// InterfaceWriter interfaceWriter = new InterfaceWriter(IRootResourceManagement.class, "/mqnaas/resources");
		//
		// Class<?> apiInterface = interfaceWriter.toClass();
		//
		// APIMapper mapper = RESTAPIGenerator.createAPIInterface(apiInterface, interfaceWriter, IRootResourceManagement.class, rrm);
		//
		// sf.setResourceClasses(apiInterface);
		//
		// Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { apiInterface }, new APITestInvocationHandler(mapper));
		//
		// sf.setResourceProvider(apiInterface, new SingletonResourceProvider(proxy));

		// JunosInterfaceManagement interfaceManagement = new JunosInterfaceManagement();
		// interfaceManagement.createInterface("Interface 1");
		// interfaceManagement.createInterface("Interface 2");
		// interfaceManagement.createInterface("Interface 3");
		//
		// InterfaceWriter interfaceWriter2 = new InterfaceWriter(IInterfaceManagement.class, "/mqnaas/interfaces");
		//
		// Class<?> apiInterface2 = interfaceWriter2.toClass();
		//
		// APIMapper mapper2 = RESTAPIGenerator.createAPIInterface(apiInterface2, interfaceWriter2, IInterfaceManagement.class, interfaceManagement);
		//
		// sf.setResourceClasses(apiInterface2);
		//
		// Object proxy2 = Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { apiInterface2 }, new
		// APITestInvocationHandler(mapper2));
		//
		// sf.setResourceProvider(apiInterface2, new SingletonResourceProvider(proxy2));

	}

	public static void main(String[] args) {
		try {
			RootResourceDescriptor d = RootResourceDescriptor.create(new Specification(Specification.Type.NETWORK, "The best", "1.0"));
			System.out.println(org.i2cat.utils.JAXBSerializer.toXml(d));

			new APITest();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package org.mqnaas.core;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.mqnaas.core.api.RootResourceDescriptor;

public class APITest {

	// public interface Echo {
	// @GET
	// @Path("/echo/{input}")
	// @Produces("text/plain")
	// public String echo(@PathParam("input") String input);
	// }

	// public class EchoService implements Echo {
	// public String echo(final String input) {
	// return "Hello, world! You said: " + input;
	// }
	// }

	public APITest() throws Exception {
		// JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
		// sf.setResourceClasses(Echo.class);
		// sf.setResourceProvider(Echo.class, new SingletonResourceProvider(new EchoService()));

		// sf.setResourceClasses(IResourceManagement.class);
		// sf.setResourceProvider(IResourceManagement.class, new SingletonResourceProvider(new ResourceManagement()));
		//
		// sf.setAddress("http://localhost:9000/");

		try {

			RootResourceDescriptor rrd = new RootResourceDescriptor();

			// File file = new File("/Users/georg/tmp/rootresourcedescriptor1.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(RootResourceDescriptor.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// jaxbMarshaller.marshal(rrd, file);
			jaxbMarshaller.marshal(rrd, System.out);

		} catch (JAXBException e) {
			e.printStackTrace();
		}

		// sf.create();

		// while (1 == 1) {
		// Thread.sleep(2000);
		// }
	}

	public static void main(String[] args) {
		try {
			new APITest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

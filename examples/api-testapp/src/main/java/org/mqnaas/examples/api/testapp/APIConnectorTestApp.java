package org.mqnaas.examples.api.testapp;

//import java.io.DataInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLConnection;

import org.mqnaas.api.IAPIConnector;
import org.mqnaas.api.IRESTAPIProvider;
import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IRootResourceManagement;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.examples.sampleresource.ISampleCapability;
import org.mqnaas.examples.sampleresource.ISampleMgmtCapability;
import org.mqnaas.examples.sampleresource.SampleResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple REST API publication test class. To be deleted.
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * 
 */
public class APIConnectorTestApp implements IApplication {

	private static final Logger	log	= LoggerFactory.getLogger(APIConnectorTestApp.class);

	@DependingOn
	IRootResourceManagement		rootResourceManagement;

	@DependingOn
	IExecutionService			executionService;

	@DependingOn
	IRESTAPIProvider			restApiProvider;

	@DependingOn
	IServiceProvider			serviceProvider;
	
	/**
	 * Forces this App to be activated AFTER apiConnector
	 */
	@DependingOn
	IAPIConnector apiConnector;

	@Override
	public void activate() {

		log.info("Start API test");

		try {
			// restApiProvider.publish(rootResourceManagement, IRootResourceManagement.class, "/mqnaas/rootResources");
			// restApiProvider.publish(serviceProvider, IServiceProvider.class, "/mqnaas/services");
			// restApiProvider.publish(executionService, IExecutionService.class, "/mqnaas/");
			
			ISampleMgmtCapability coreSampleMgmtCapability = serviceProvider.getCapability(rootResourceManagement.getCore(), ISampleMgmtCapability.class);
			
			SampleResource sampleResource = new SampleResource("s0");
			coreSampleMgmtCapability.addSampleResource(sampleResource);
			
			// check capabilities have been bound to sampleResource
			// this method throws an exception if specified capability could be found.
			ISampleMgmtCapability sampleMgmtCapability = serviceProvider.getCapability(sampleResource, ISampleMgmtCapability.class);
			serviceProvider.getCapability(sampleResource, ISampleCapability.class);
			
			// TODO wait for capabilities API being published
			//checkEndpointWADLIsAccessible("http://localhost:9000/mqnaas/ISampleMgmtCapability/s0/ISampleMgmtCapability");
			//checkEndpointWADLIsAccessible("http://localhost:9000/mqnaas/ISampleMgmtCapability/s0/ISampleCapability");
			
			
			sampleMgmtCapability.addSampleResource(new SampleResource("s1"));
			// TODO wait for capabilities API being published
			//checkEndpointWADLIsAccessible("http://localhost:9000/mqnaas/ISampleMgmtCapability/s0/ISampleMgmtCapability/s1/ISampleMgmtCapability");
			//checkEndpointWADLIsAccessible("http://localhost:9000/mqnaas/ISampleMgmtCapability/s0/ISampleMgmtCapability/s1/ISampleCapability");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void deactivate() {
		try {
	
			ISampleMgmtCapability coreSampleMgmtCapability = serviceProvider.getCapability(rootResourceManagement.getCore(), ISampleMgmtCapability.class);
			SampleResource s0 = coreSampleMgmtCapability.getSampleResources().get(0);
			
			
			ISampleMgmtCapability sampleMgmtCapability = serviceProvider.getCapability(s0, ISampleMgmtCapability.class);
			SampleResource s1 = sampleMgmtCapability.getSampleResources().get(0);
			
			sampleMgmtCapability.removeSampleResource(s1);
			coreSampleMgmtCapability.removeSampleResource(s0);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	private void checkEndpointWADLIsAccessible(String urlStr) {
//    	DataInputStream inStream = null;
//    	try {
//            // Create connection
//            URL url = new URL(urlStr + "?" + "_wadl");
//            URLConnection connection = url.openConnection();
//           
//            connection.setReadTimeout(READ_TIMEOUT);
//             inStream = new DataInputStream(connection.getInputStream());
//            
//            HttpURLConnection httpConnection = (HttpURLConnection) connection;
//            int status = httpConnection.getResponseCode();
//            
//            if (status == -1) {
//            	Assert.fail("Invalid HTTP response");
//            }
//            if (status >= 400 && status < 600) {
//            	InputStream error = httpConnection.getErrorStream();
//            	Assert.fail("Error respnse: HTTP code " + status + " error: " + error.toString());
//            }   
//    	} catch(IOException ioe) {
//    		Assert.fail(ioe.getMessage());
//    	} finally {
//    		if (inStream != null) {
//    			try {
//					inStream.close();
//				} catch (IOException e) {
//					// ignored
//				}
//    		}
//    	}
//    }

}

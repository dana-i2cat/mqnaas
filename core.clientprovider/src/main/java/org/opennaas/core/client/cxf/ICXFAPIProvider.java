package org.opennaas.core.client.cxf;

import org.opennaas.core.clientprovider.api.apiclient.IAPIClientProvider;

/**
 * Requires type T being JAX-RS annotated.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public interface ICXFAPIProvider extends IAPIClientProvider<CXFConfiguration> {

}

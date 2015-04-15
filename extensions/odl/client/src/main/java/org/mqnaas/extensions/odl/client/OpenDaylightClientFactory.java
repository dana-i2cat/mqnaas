package org.mqnaas.extensions.odl.client;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: OpenDaylight
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.common.util.ProxyClassLoader;
import org.mqnaas.extensions.odl.client.helium.flowprogrammer.api.IOpenDaylightFlowProgrammerNorthbound;

/**
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * @author Adrian Rosello (i2CAT)
 *
 */
public class OpenDaylightClientFactory {

    public IOpenDaylightFlowProgrammerNorthbound createClient(String addressUri, String username, String password) {

        // create CXF client
        ProxyClassLoader classLoader = new ProxyClassLoader(IOpenDaylightFlowProgrammerNorthbound.class.getClassLoader());
        classLoader.addLoader(JAXRSClientFactoryBean.class.getClassLoader());

        JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
        bean.setAddress(addressUri);
        bean.setUsername(username);
        bean.setPassword(password);
        bean.setResourceClass(IOpenDaylightFlowProgrammerNorthbound.class);
        bean.setClassLoader(classLoader);

        return (IOpenDaylightFlowProgrammerNorthbound) bean.create();
    }
}

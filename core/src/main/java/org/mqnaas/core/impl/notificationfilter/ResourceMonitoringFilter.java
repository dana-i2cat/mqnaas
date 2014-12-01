package org.mqnaas.core.impl.notificationfilter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IObservationFilter;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IService;

/**
 * Matches services annotated with annotation given in the constructor.
 * 
 * This filters prepares parameters for methods in {@link org.mqnaas.core.api.IResourceManagementListener}
 */
public class ResourceMonitoringFilter implements IObservationFilter {

	private Class<? extends Annotation>	classAnnotation;

	public ResourceMonitoringFilter(Class<? extends Annotation> classAnnotation) {
		this.classAnnotation = classAnnotation;
	}

	@Override
	public boolean observes(IService service, Object[] args) {
		return service.getMetadata().hasAnnotation(classAnnotation);
	}

	/**
	 * Retrieves affected IResource among service parameters and result. Retrieves IApplication managing affected IResource from the service itself.
	 * 
	 * The resource is retrieved following this algorithm: - Look at observed services parameters, fist one implementing IResource is taken as the
	 * affected resource (if any) - If not yet found, if result implements IResource it is taken as affected resource. - Null otherwise
	 * 
	 * @return an array with affected resource and the IApplication managing it.
	 */
	@Override
	public Object[] getParameters(IService service, Object[] args, Object result) {

		// retrieve resource
		IResource affectedResource = null;
		if (args != null) {
			for (Object arg : args) {
				if (arg instanceof IResource) {
					affectedResource = (IResource) arg;
					break;
				}
			}
		}
		if (affectedResource == null)
			if (result instanceof IResource)
				affectedResource = (IResource) result;

		// retrieve application holding the resource
		IApplication resourceHolder = service.getMetadata().getApplication();
		
		Class<? extends IApplication> resourceHolderInterface = service.getMetadata().getApplicationInterface();

		List<Object> parameters = new ArrayList<Object>(3);
		parameters.add(affectedResource);
		parameters.add(resourceHolder);
		parameters.add(resourceHolderInterface);
		return parameters.toArray();
	}

	@Override
	public String toString() {
		return "annotation == " + classAnnotation.getSimpleName();
	}

}

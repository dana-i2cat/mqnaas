package org.mqnaas.core.impl.notificationfilter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.mqnaas.core.api.IObservationFilter;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.annotations.AddsResource;

/**
 * The AddAnnotationFilter filters all Services which are annotated with the {@link AddsResource}.
 */
public class ResourceMonitoringFilter implements IObservationFilter {

	private Class<? extends Annotation>	classAnnotation;

	public ResourceMonitoringFilter(Class<? extends Annotation> classAnnotation) {
		this.classAnnotation = classAnnotation;
	}

	@Override
	public boolean observes(IService service, Object[] args) {
		return service.hasAnnotation(classAnnotation);
	}

	@Override
	public Object[] getParameters(IService service, Object[] args, Object result) {

		List<Object> parameters = new ArrayList<Object>();

		if (args != null) {
			for (Object arg : args) {
				if (arg instanceof IResource)
					parameters.add(arg);
			}
		}

		if (result instanceof IResource)
			parameters.add(result);

		return parameters.toArray();
	}

	@Override
	public String toString() {
		return "annotation == " + classAnnotation.getSimpleName();
	}

}

package org.mqnaas.api.writers;

/*
 * #%L
 * MQNaaS :: REST API Provider
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.i2cat.utils.StringBuilderUtils;
import org.objectweb.asm.AnnotationVisitor;

class AnnotationParamWriter extends AbstractWriter {

	private String	name;
	private Object	value;

	AnnotationParamWriter(String name, Object value) {
		this.name = name;

		if (value == null)
			throw new NullPointerException("No need to add a parameter with a null value!");

		if (value instanceof Class) {
			this.value = org.objectweb.asm.Type.getType(toBytecodeName((Class<?>) value));
		} else {
			this.value = value;
		}
	}

	AnnotationParamWriter(String name, Object[] value) {
		this(name, (Object) value);
	}

	public void writeTo(AnnotationVisitor av) {

		if (isArray()) {
			Object[] values = (Object[]) value;

			AnnotationVisitor arrayVisitor = av.visitArray(name);
			for (Object value : values) {
				arrayVisitor.visit(null, value);
			}

			arrayVisitor.visitEnd();

		} else {

			av.visit(name, value);
		}
	}

	private boolean isArray() {
		return value.getClass().isArray();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(name);

		sb.append("=");

		if (isArray()) {
			sb.append("[");
			StringBuilderUtils.append(sb, (Object[]) value);
			sb.append("]");
		} else {
			sb.append(value);
		}

		return sb.toString();
	}
}
package org.mqnaas.api.writers;

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
package org.mqnaas.api.writers;

import java.lang.annotation.Annotation;

import org.i2cat.utils.StringBuilderUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

class AnnotationWriter extends AbstractWriter {

	private int						parameterIndex;

	private Class<?>				annotation;
	private AnnotationParamWriter[]	params;

	AnnotationWriter(Class<? extends Annotation> annotation, AnnotationParamWriter... params) {
		this(-1, annotation, params);
	}

	AnnotationWriter(int parameterIndex, Class<? extends Annotation> annotation, AnnotationParamWriter... params) {
		this.parameterIndex = parameterIndex;
		this.annotation = annotation;
		this.params = params;
	}

	public int getParameterIndex() {
		return parameterIndex;
	}

	public boolean isParameterAnnotation() {
		return parameterIndex != -1;
	}

	public void writeTo(ClassVisitor cv) {

		AnnotationVisitor av = cv.visitAnnotation(toBytecodeName(annotation), true);

		if (params != null) {
			for (AnnotationParamWriter param : params) {
				param.writeTo(av);
			}
		}

		av.visitEnd();
	}

	public void writeTo(MethodVisitor mv) {

		if (params != null) {

			AnnotationVisitor av;

			if (!isParameterAnnotation()) {
				av = mv.visitAnnotation(toBytecodeName(annotation), true);
			} else {
				av = mv.visitParameterAnnotation(parameterIndex, toBytecodeName(annotation), true);
			}

			for (AnnotationParamWriter param : params) {
				param.writeTo(av);
			}

			av.visitEnd();
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("@");
		sb.append(annotation.getName());

		if (params != null) {
			sb.append("(");
			StringBuilderUtils.append(sb, params);
			sb.append(")");
		}

		return sb.toString();
	}

}
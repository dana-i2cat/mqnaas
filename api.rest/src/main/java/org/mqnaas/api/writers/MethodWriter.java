package org.mqnaas.api.writers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.i2cat.utils.StringBuilderUtils;
import org.i2cat.utils.StringBuilderUtils.ValueExtractor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class MethodWriter extends AbstractWriter {

	private String							name;
	private Class<?>						returnType;
	private Class<?>[]						paramTypes;
	private List<AnnotationWriter>			annotationWriters;

	private static ValueExtractor<Class<?>>	BYTECODE_NAME_EXTRACTOR	= new ValueExtractor<Class<?>>() {
																		@Override
																		public Object getValueToAppend(Class<?> clazz) {
																			return toBytecodeName(clazz);
																		}
																	};

	MethodWriter(String name, Class<?> returnType, Class<?>[] paramTypes) {
		this.name = name;
		this.returnType = returnType;
		this.paramTypes = paramTypes;
		this.annotationWriters = new ArrayList<AnnotationWriter>(0);
	}

	MethodWriter(String name, Class<?> returnType, Class<?>[] paramTypes, AnnotationWriter... annotationWriters) {
		this(name, returnType, paramTypes);
		this.annotationWriters = new ArrayList<AnnotationWriter>(Arrays.asList(annotationWriters));
	}

	public void addAnnotationWriter(AnnotationWriter annotationWriter) {
		annotationWriters.add(annotationWriter);
	}

	public void writeTo(ClassVisitor cv) {

		StringBuilder signatureBuilder = StringBuilderUtils.create("", BYTECODE_NAME_EXTRACTOR, paramTypes);
		signatureBuilder.insert(0, "(").append(")").append(toBytecodeName(returnType));

		MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, name, signatureBuilder.toString(),
				null, null);

		for (AnnotationWriter annotationWriter : annotationWriters) {
			annotationWriter.writeTo(mv);
		}

		mv.visitEnd();
	}

	@Override
	public String toString() {

		Map<Integer, AnnotationWriter> parameterAnnotations = new HashMap<Integer, AnnotationWriter>();
		List<AnnotationWriter> methodAnnotations = new ArrayList<AnnotationWriter>();

		for (AnnotationWriter writer : annotationWriters) {
			if (writer.isParameterAnnotation()) {
				parameterAnnotations.put(writer.getParameterIndex(), writer);
			} else {
				methodAnnotations.add(writer);
			}
		}

		StringBuilder sb = StringBuilderUtils.create("\n", methodAnnotations).append("\n");

		sb.append(returnType.getName()).append(" ").append(name).append("(");

		for (int i = 0; i < paramTypes.length; i++) {
			if (i > 0)
				sb.append(", ");
			if (parameterAnnotations.containsKey(i)) {
				sb.append(parameterAnnotations.get(i)).append(" ");
			}

			sb.append(StringBuilderUtils.CLASSNAME_EXTRACTOR.getValueToAppend(paramTypes[i]));
		}

		sb.append(")");

		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public Class<?>[] getParameterTypes() {
		return paramTypes;
	}
}
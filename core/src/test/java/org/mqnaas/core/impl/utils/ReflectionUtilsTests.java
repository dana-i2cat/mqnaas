package org.mqnaas.core.impl.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ReflectionUtilsTests {

	@Test
	public void testGetAnnotationFieldsWithSimpelClass() {
		List<Field> fields = ReflectionUtils.getAnnotationFields(SimpleClass.class, TestAnnotation.class);
		Assert.assertEquals("SimpleClass does not contain any field annotated with TestAnnotation", 0, fields.size());

		fields = ReflectionUtils.getAnnotationFields(SimpleClassWithAnnotation.class, TestAnnotation.class);
		Assert.assertEquals("SimpleClassWithAnnotation contains a field annotated with TestAnnotation", 1, fields.size());

		fields = ReflectionUtils.getAnnotationFields(SubClass.class, TestAnnotation.class);
		Assert.assertEquals("SubClass contains a superclass field annotated with TestAnnotation", 1, fields.size());

		fields = ReflectionUtils.getAnnotationFields(SubSubClass.class, TestAnnotation.class);
		Assert.assertEquals("SubClass contains a field and a superclass field annotated with TestAnnotation", 2, fields.size());

		fields = ReflectionUtils.getAnnotationFields(SubSubClass.class, Target.class);
		Assert.assertEquals("SubClass contains no fields annotated with Target", 0, fields.size());

	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface TestAnnotation {

	}

	private class SimpleClass {
	}

	private class SimpleClassWithAnnotation {

		@TestAnnotation
		private Object	testField;

	}

	private class SubClass extends SimpleClassWithAnnotation {

		@SuppressWarnings("unused")
		private String	stringField;
	}

	private class SubSubClass extends SubClass {

		@TestAnnotation
		public Object	anotherField;
	}

}

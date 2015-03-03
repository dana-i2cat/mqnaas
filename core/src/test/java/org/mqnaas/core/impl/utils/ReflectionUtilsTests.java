package org.mqnaas.core.impl.utils;

/*
 * #%L
 * MQNaaS :: Core
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

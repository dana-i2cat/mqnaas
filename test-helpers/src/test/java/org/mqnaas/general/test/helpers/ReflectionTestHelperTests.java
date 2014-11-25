package org.mqnaas.general.test.helpers;

import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.general.test.helpers.reflection.ReflectionTestHelper; 

/**
 * Unit tests of {@link ReflectionTestHelper}
 * 
 * @author Julio Carlos Barrera
 *
 */
public class ReflectionTestHelperTests {

	private static final String	TEST_STRING	= "test_string";

	@Test
	public void testInjectPrivateField() throws SecurityException, IllegalArgumentException, IllegalAccessException {
		TestClass testClass = new TestClass();
		ReflectionTestHelper.injectPrivateField(testClass, TEST_STRING, "privateCharSequence");
		Assert.assertEquals("Field must be injected.", TEST_STRING, testClass.getPrivateCharSequence());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadTypeInjectPrivateField() throws SecurityException, IllegalArgumentException, IllegalAccessException {
		TestClass testClass = new TestClass();
		ReflectionTestHelper.injectPrivateField(testClass, new Object(), "privateCharSequence");

		// with the parameterized approach this error could not be produced in runtime because it is produced at compile time.
		//
		// in this example it produces this compiler error:
		// "The parameterized method <TestClass, String>injectPrivateField(TestClass, String, String) of type ReflectionTestHelper
		// is not applicable for the arguments (TestClass, Object, String)"
		//
		// ReflectionTestHelper.<TestClass, String> injectPrivateField(testClass, new Object(), "privateCharSequence");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadFieldNameInjectPrivateField() throws SecurityException, IllegalArgumentException, IllegalAccessException {
		TestClass testClass = new TestClass();
		ReflectionTestHelper.injectPrivateField(testClass, TEST_STRING, "BAD_FIELD_NAME");
	}

	@Test
	public void testParameterizedInjectPrivateField() throws SecurityException, IllegalArgumentException, IllegalAccessException {
		TestClass testClass = new TestClass();
		ReflectionTestHelper.<TestClass, String> injectPrivateField(testClass, TEST_STRING, "privateCharSequence");
		Assert.assertEquals("Field must be injected.", TEST_STRING, testClass.getPrivateCharSequence());
	}

}

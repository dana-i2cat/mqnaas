package org.i2cat.utils;

import java.util.Arrays;
import java.util.Collection;

public abstract class StringBuilderUtils {

	public interface ValueExtractor<T> {
		Object getValueToAppend(T value);
	}

	/**
	 * Extracts a {@link Class}'s name, e.g. {@link Class#getName()}
	 */
	public static ValueExtractor<Class<?>>	CLASSNAME_EXTRACTOR	= new ValueExtractor<Class<?>>() {
																	@Override
																	public Object getValueToAppend(Class<?> value) {
																		return value.getName();
																	}
																};

	private static String					DEFAULT_DELIMITER	= ", ";

	public static <T> StringBuilder create(T[] values) {
		return create(DEFAULT_DELIMITER, null, Arrays.asList(values));
	}

	public static <T> StringBuilder create(ValueExtractor<T> extractor, T[] values) {
		return create(DEFAULT_DELIMITER, extractor, Arrays.asList(values));
	}

	public static <T> StringBuilder create(String delimiter, T[] values) {
		return create(delimiter, null, values != null ? Arrays.asList(values) : null);
	}

	public static <T> StringBuilder create(String delimiter, Collection<T> values) {
		return create(delimiter, null, values);
	}

	public static <T> StringBuilder create(String delimiter, ValueExtractor<T> extractor, T[] values) {
		return create(delimiter, extractor, Arrays.asList(values));
	}

	public static <T> StringBuilder create(String delimiter, ValueExtractor<T> extractor, Collection<T> values) {
		StringBuilder sb = new StringBuilder();
		return append(sb, delimiter, extractor, values);
	}

	public static <T> StringBuilder append(StringBuilder sb, T[] values) {
		return append(sb, DEFAULT_DELIMITER, null, values);
	}

	public static <T> StringBuilder append(StringBuilder sb, ValueExtractor<T> extractor, T[] values) {
		return append(sb, DEFAULT_DELIMITER, extractor, values);
	}

	public static <T> StringBuilder append(StringBuilder sb, String delimiter, Collection<T> values) {
		return append(sb, delimiter, null, values);
	}

	public static <T> StringBuilder append(StringBuilder sb, String delimiter, T[] values) {
		return append(sb, delimiter, null, values);
	}

	public static <T> StringBuilder append(StringBuilder sb, String delimiter, ValueExtractor<T> extractor, T[] values) {
		return append(sb, delimiter, extractor, Arrays.asList(values));
	}

	public static <T> StringBuilder append(StringBuilder sb, String delimiter, ValueExtractor<T> extractor, Collection<T> values) {
		int i = 0;

		if (values != null) {
			for (T value : values) {
				if (i > 0) {
					sb.append(delimiter);
				}
				sb.append(extractor != null ? extractor.getValueToAppend(value) : value);
				i++;
			}
		}

		return sb;
	}

}

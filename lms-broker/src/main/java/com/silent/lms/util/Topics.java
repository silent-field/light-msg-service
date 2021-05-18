package com.silent.lms.util;

/**
 *
 * @author gy
 * @date 2021/5/12.
 * @version 1.0
 * @description:
 */
public class Topics {
	/**
	 * The multi-level wildcard character.
	 */
	private static final char MULTI_LEVEL_WILDCARD = '#';

	/**
	 * The single-level wildcard character.
	 */
	private static final char SINGLE_LEVEL_WILDCARD = '+';

	/**
	 * 是否包含通配符
	 * @param topic
	 * @return
	 */
	public static boolean containsWildcard(final String topic) {
		return (topic.indexOf(MULTI_LEVEL_WILDCARD) != -1) ||
				(topic.indexOf(SINGLE_LEVEL_WILDCARD) != -1);
	}
}

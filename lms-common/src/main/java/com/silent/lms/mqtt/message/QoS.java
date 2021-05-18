package com.silent.lms.mqtt.message;

import com.google.common.collect.Range;
import com.silent.lms.dict.Dict;

/**
 * @author gy
 * @version 1.0
 * @date 2021/4/30.
 * @description:
 */
public enum QoS implements Dict<Integer> {
	/**
	 * 最多一次. 消息会被消费一次或者没消费，尽最大努力消费
	 */
	AT_MOST_ONCE(0, "最多一次"),
	/**
	 * 最少一次. 消息会至少被消费一次
	 */
	AT_LEAST_ONCE(1, "最少一次"),
	/**
	 * 精确一次. 消息会被消费，且只会一次
	 */
	EXACTLY_ONCE(2, "精确一次"),
	;

	QoS(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	final Integer code;
	final String desc;

	public Integer getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public static QoS getByCode(Integer code) {
		for (QoS p : QoS.values()) {
			if (p.getCode().equals(code)) {
				return p;
			}
		}
		throw new IllegalArgumentException("Invalid Message Type: " + code);
	}

	private static final Range<Integer> VALUE_RANGE = Range.closed(0, 2);

	public static boolean isValidValue(Integer code){
		return VALUE_RANGE.contains(code);
	}
}

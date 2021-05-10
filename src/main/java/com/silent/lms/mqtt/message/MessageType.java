package com.silent.lms.mqtt.message;

import com.silent.lms.dict.Dict;

/**
 * @author gy
 * @version 1.0
 * @date 2021/4/30.
 * @description:
 */
public enum MessageType implements Dict<Integer> {
	/**
	 * 0：初始状态
	 * CONNECT – 连接请求
	 * CONNACK – 确认连接请求
	 * PUBLISH – 发布消息
	 * PUBACK –发布确认
	 * PUBREC – 发布收到（QoS 2，第一步）
	 * PUBREL – 发布释放（QoS 2，第二步）
	 * PUBCOMP – 发布完成（QoS 2，第三步）
	 * SUBSCRIBE - 订阅主题
	 * SUBACK – 订阅确认
	 * UNSUBSCRIBE –取消订阅
	 * UNSUBACK – 取消订阅确认
	 * PINGREQ – 心跳请求
	 * PINGRESP – 心跳响应
	 * DISCONNECT –断开连接
	 * AUTH - 鉴权
	 *
	 * 1~14是MQTT相关协议定义
	 */
	INITIAL(0, "初始"),
	CONNECT(1, "连接请求"),
	CONNACK(2, "确认连接请求"),
	PUBLISH(3, "发布消息"),
	PUBACK(4, "发布确认"),
	PUBREC(5, "发布收到（QoS 2，第一步）"),
	PUBREL(6, "发布释放（QoS 2，第二步）"),
	PUBCOMP(7, "发布完成（QoS 2，第三步）"),
	SUBSCRIBE(8, "订阅主题"),
	SUBACK(9, "订阅确认"),
	UNSUBSCRIBE(10, "取消订阅"),
	UNSUBACK(11, "取消订阅确认"),
	PINGREQ(12, "心跳请求"),
	PINGRESP(13, "心跳响应"),
	DISCONNECT(14, "断开连接"),
	AUTH(15, "鉴权"),
	;

	MessageType(Integer code, String desc) {
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

	public static MessageType getByCode(Integer code) {
		for (MessageType p : MessageType.values()) {
			if (p.getCode().equals(code)) {
				return p;
			}
		}
		throw new IllegalArgumentException("Invalid Message Type: " + code);
	}
}

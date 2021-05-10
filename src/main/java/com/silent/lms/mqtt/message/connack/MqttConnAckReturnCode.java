package com.silent.lms.mqtt.message.connack;

import com.silent.lms.dict.Dict;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/3
 * @description:
 */
public enum MqttConnAckReturnCode implements Dict<Integer> {
    ACCEPTED(0,""),
    REFUSED_UNACCEPTABLE_PROTOCOL_VERSION(1,""),
    REFUSED_IDENTIFIER_REJECTED(2,""),
    REFUSED_SERVER_UNAVAILABLE(3,""),
    REFUSED_BAD_USERNAME_OR_PASSWORD(4,""),
    REFUSED_NOT_AUTHORIZED(5,""),
    ;

    MqttConnAckReturnCode(Integer code, String desc) {
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
}

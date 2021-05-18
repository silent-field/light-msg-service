package com.silent.lms.mqtt.message.id;

import com.silent.lms.annotations.ThreadSafe;
import com.silent.lms.mqtt.message.id.exception.NoIdAvailableException;

/**
 * @author gy
 * @version 1.0
 * @date 2021/4/30.
 * @description: 用于生成MQTT的消息ID，ID可回收，范围是1~65535
 * <p>
 * PUBLISH（QoS > 0时）， PUBACK，PUBREC，PUBREL，PUBCOMP，SUBSCRIBE, SUBACK，UNSUBSCRIBE，UNSUBACK。
 * SUBSCRIBE，UNSUBSCRIBE和PUBLISH（QoS大于0）控制报文必须包含一个非零的16位报文标识符（Packet Identifier）[MQTT-2.3.1-1]。
 * 客户端每次发送一个新的这些类型的报文时都必须分配一个当前未使用的报文标识符 [MQTT-2.3.1-2]。
 * 如果一个客户端要重发这个特殊的控制报文，在随后重发那个报文时，它必须使用相同的标识符。
 * 当客户端处理完这个报文对应的确认后，这个报文标识符就释放可重用。
 * QoS 1的PUBLISH对应的是PUBACK，QoS 2的PUBLISH对应的是PUBCOMP，与SUBSCRIBE或UNSUBSCRIBE对应的分别是SUBACK或UNSUBACK [MQTT-2.3.1-3]。
 * 发送一个QoS 0的PUBLISH报文时，相同的条件也适用于服务端 [MQTT-2.3.1-4]。
 * QoS等于0的PUBLISH报文不能包含报文标识符 [MQTT-2.3.1-5]。
 * </p>
 */
@ThreadSafe
public interface MessageIDProducer {
    /**
     * 获取一个ID，如果没有可用ID，抛出NoIdAvailableException异常
     * <p>
     * 调用方需要归回ID
     * </p>
     *
     * @return
     * @throws NoIdAvailableException
     */
    @ThreadSafe
    int takeId() throws NoIdAvailableException;

    /**
     * 指定ID，如果指定的ID可用则返回指定ID，如果已被使用，返回一个可用的ID，如果没有可用ID，抛出NoIdAvailableException异常
     * <p>
     * 调用方需要归回ID
     * </p>
     *
     * @param id
     * @return
     * @throws NoIdAvailableException
     */
    @ThreadSafe
    int takeIfAvailable(int id) throws NoIdAvailableException;

    /**
     * 当ID不再需要使用的时候归还ID
     *
     * @param id
     */
    @ThreadSafe
    void returnId(int id);
}

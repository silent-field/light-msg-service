package com.silent.lms.mqtt.message;

import com.google.common.util.concurrent.Striped;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.configuration.InternalConfigurations;
import com.silent.lms.mqtt.message.id.MessageIDProducer;
import com.silent.lms.mqtt.message.id.OrderedMessageIDProducerImpl;

import javax.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author gy
 * @version 1.0
 * @date 2021/4/30.
 * @description: 消息ID提供者
 */
@Singleton
public class MessageIDPoolProviders {
	/**
	 * 使用Guava Striped来控制并发锁，实现并发操作producers
	 * <p>
	 * 使用不同的key(String)可能拿到相同的ReadWriteLock
	 * </p>
	 */
	private final Striped<ReadWriteLock> lock;
	private final ConcurrentHashMap<String, MessageIDProducer> producers = new ConcurrentHashMap<>();

	// -------------------- 单例
	MessageIDPoolProviders() {
		lock = Striped.readWriteLock(InternalConfigurations.MESSAGE_ID_PRODUCER_LOCK_SIZE);
	}

	public static MessageIDPoolProviders instance = InstanceHolder.instance;

	private static class InstanceHolder {
		private static final MessageIDPoolProviders instance = new MessageIDPoolProviders();
	}

	// -------------------- 单例

	/**
	 * 为一个Client返回一个ID池。如果当前不存在ID池，会新建一个新ID池并关联这个Client
	 *
	 * @param clientId client标志
	 * @return
	 */
	@NotNull
	public MessageIDProducer forClient(final @NotNull String clientId) {
		final Lock readLock = this.lock.get(clientId).readLock();
		readLock.lock();

		try {
			MessageIDProducer idProducer = producers.get(clientId);

			if (idProducer == null) {
				idProducer = producers.computeIfAbsent(clientId, s -> new OrderedMessageIDProducerImpl());
			}

			return idProducer;
		} finally {
			readLock.unlock();
		}
	}

	@Nullable
	public MessageIDProducer forClientOrNull(final @NotNull String clientId) {
		final Lock readLock = this.lock.get(clientId).readLock();
		readLock.lock();
		try {
			return producers.get(clientId);
		} finally {
			readLock.unlock();
		}
	}

	public void remove(final @NotNull String clientId) {
		final Lock writeLock = this.lock.get(clientId).writeLock();
		writeLock.lock();
		try {
			producers.remove(clientId);
		} finally {
			writeLock.unlock();
		}
	}

	public int size() {
		return producers.size();
	}
}

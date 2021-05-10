package com.silent.lms.mqtt.message.id;

import com.silent.lms.annotations.ThreadSafe;
import com.silent.lms.configuration.InternalConfigurations;
import com.silent.lms.mqtt.message.id.exception.NoIdAvailableException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author gy
 * @version 1.0
 * @date 2021/4/30.
 * @description: 用于生成MQTT的消息ID，ID可回收，范围是1~65535，ID生成是有序的
 */
@Log4j2
@ThreadSafe
public class OrderedMessageIDProducerImpl implements MessageIDProducer {


	/**
	 * 可以缓存异常，因为对任何堆栈跟踪都不感兴趣
	 */
	private static final NoIdAvailableException NO_MESSAGE_ID_AVAILABLE_EXCEPTION = new NoIdAvailableException();

	static {
		/**
		 * 清除堆栈信息
		 */
		NO_MESSAGE_ID_AVAILABLE_EXCEPTION.setStackTrace(new StackTraceElement[0]);
	}

	/**
	 * 环形计数器
	 */
	private final AtomicInteger ringCounter = new AtomicInteger();

	private final ReentrantLock lock = new ReentrantLock();

	/**
	 * 已使用过的ID，需要在ID归还后可复用
	 */
	@Getter
	private final Set<Integer> usedMessageIds = new HashSet<>(30);

	@ThreadSafe
	@Override
	public synchronized int takeId() throws NoIdAvailableException {
		lock.lock();
		try {
			return take();
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	@ThreadSafe
	@Override
	public int takeIfAvailable(int id) throws NoIdAvailableException {
		lock.lock();
		try {
			if (!InternalConfigurations.MESSAGE_ID_RANGE.contains(id)) {
				throw new IllegalArgumentException("MessageID must be between 1 to 65535");
			}

			if (usedMessageIds.contains(id)) {
				return take();
			}

			usedMessageIds.add(id);

			if (id > ringCounter.get()) {
				ringCounter.set(id);
			}

			return id;
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 获取一个可用ID
	 *
	 * @return
	 * @throws NoIdAvailableException
	 */
	private int take() throws NoIdAvailableException {
		if (usedMessageIds.size() >= InternalConfigurations.MESSAGE_ID_UPPER_LIMIT) {
			throw NO_MESSAGE_ID_AVAILABLE_EXCEPTION;
		}

		// 重置循环计数器，最大值是65535
		ringCounter.compareAndSet(InternalConfigurations.MESSAGE_ID_UPPER_LIMIT, InternalConfigurations.MESSAGE_ID_LOWER_LIMIT);

		// 按顺序增加counter值，直到找到尚未使用的消息ID
		int newValue;
		do {
			newValue = ringCounter.incrementAndGet();
		}
		while (usedMessageIds.contains(newValue) && newValue < InternalConfigurations.MESSAGE_ID_UPPER_LIMIT);

		// 需要保证ID值不可以超过65535，如果newValue>65535说明没有可用的ID值
		if (newValue > InternalConfigurations.MESSAGE_ID_UPPER_LIMIT) {
			ringCounter.compareAndSet(InternalConfigurations.MESSAGE_ID_UPPER_OVERFLOW, 0);
			throw NO_MESSAGE_ID_AVAILABLE_EXCEPTION;
		}

		usedMessageIds.add(newValue);

		return newValue;
	}

	@ThreadSafe
	@Override
	public void returnId(int id) {
		lock.lock();
		try {
			if (!InternalConfigurations.MESSAGE_ID_RANGE.contains(id)) {
				throw new IllegalArgumentException("MessageID must be between 1 to 65535");
			}

			final boolean removed = usedMessageIds.remove(id);

			if (!removed) {
				log.warn("消息ID {} 归还失败，可能已经归还过，意味着可能ack过DUP消息", id);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}
}

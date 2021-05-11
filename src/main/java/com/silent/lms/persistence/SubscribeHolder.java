package com.silent.lms.persistence;

import com.google.common.util.concurrent.Striped;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.bootstrap.netty.ChannelAttributes;
import com.silent.lms.mqtt.message.subscribe.Subscribe;
import com.silent.lms.mqtt.message.subscribe.Topic;
import com.silent.lms.mqtt.message.unsubscribe.Unsubscribe;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 *
 * @author gy
 * @date 2021/5/8.
 * @version 1.0
 * @description:
 */
@Log4j2
public class SubscribeHolder {
	private final @NotNull Striped<ReadWriteLock> clientIdLock;
	private final @NotNull Striped<ReadWriteLock> topicLock;
	private final @NotNull ConcurrentHashMap<String, Set<String>> topic2ClientIds = new ConcurrentHashMap<>();
	private final @NotNull ConcurrentHashMap<String, ChannelSubscribe> clientId2Channel = new ConcurrentHashMap<>();

	public void subscribe(Channel channel, Subscribe msg) {
		String clientId = channel.attr(ChannelAttributes.CLIENT_ID).get();
		List<Topic> topics = msg.getTopics();

		for (Topic topic : topics) {
			String topicDesc = topic.getTopic();
			final Lock topicWriteLock = topicLock.get(topicDesc).writeLock();
			topicWriteLock.lock();
			try {
				topic2ClientIds.computeIfAbsent(topicDesc, s -> new HashSet<>()).add(clientId);
			} finally {
				topicWriteLock.unlock();
			}
		}

		final Lock clientWriteLock = clientIdLock.get(clientId).writeLock();
		clientWriteLock.lock();
		Collection<Topic> currentTopics = null;
		try {
			ChannelSubscribe channelSubscribe = clientId2Channel.computeIfAbsent(clientId,
					s -> ChannelSubscribe.builder().clientId(clientId).channel(channel).build());
			channelSubscribe.addTopics(topics);
			currentTopics = channelSubscribe.getTopics().values();
		} catch (Exception e){
			log.error("SubscribeHolder.subscribe",e);
		} finally {
			clientWriteLock.unlock();
		}
		log.info("client : {} 订阅 topic : {} 成功，当前订阅的所有 topic : {}", channel, topics, currentTopics);
	}

	public void unsubscribe(Channel channel, Unsubscribe msg) {
		final String clientId = channel.attr(ChannelAttributes.CLIENT_ID).get();
		List<String> topics = msg.getTopics();

		final Lock clientWriteLock = clientIdLock.get(clientId).writeLock();
		clientWriteLock.lock();
		Collection<Topic> currentTopics = null;
		try {
			ChannelSubscribe channelSubscribe = clientId2Channel.get(clientId);
			if (null != channelSubscribe) {
				channelSubscribe.removeTopics(topics);
				currentTopics = channelSubscribe.getTopics().values();
			}
		} catch (Exception e){
			log.error("SubscribeHolder.unsubscribe",e);
		} finally {
			clientWriteLock.unlock();
		}
		log.info("client : {} 取消订阅 topic : {} 成功，当前订阅的所有 topic : {}", channel, topics, currentTopics);

		for (String topic : topics) {
			final Lock topicWriteLock = topicLock.get(topic).writeLock();
			topicWriteLock.lock();
			try {
				Set<String> current;
				if ((current = topic2ClientIds.get(topic)) != null) {
					current.remove(clientId);
				}
			} finally {
				topicWriteLock.unlock();
			}
		}
	}

	// ----------------------------------------------
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	private static class ChannelSubscribe {
		private String clientId;
		private Channel channel;
		@Builder.Default
		private Map<String, Topic> topics = new HashMap<>();

		public void addTopics(List<Topic> toAdds) {
			for (Topic toAdd : toAdds) {
				topics.put(toAdd.getTopic(), toAdd);
			}
		}

		public void removeTopics(List<String> toRemoves) {
			for (String toRemove : toRemoves) {
				topics.remove(toRemove);
			}
		}
	}

	// ------------------------------------------- 单例
	private SubscribeHolder() {
		clientIdLock = Striped.readWriteLock(32);
		topicLock = Striped.readWriteLock(32);
	}

	private static class InstanceHolder {
		private static final SubscribeHolder instance = new SubscribeHolder();
	}

	public static SubscribeHolder instance() {
		SubscribeHolder subscribeHolder = InstanceHolder.instance;
		return subscribeHolder;
	}
}

package com.posts.api.conf

import com.posts.api.web.sse.FeedSse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.stereotype.Service

const val SEPARATOR = "|"

@Configuration
class MessageQueueConfig {

  @Bean
  fun topicSse(): ChannelTopic {
    return ChannelTopic("sse:queue")
  }

  @Bean
  fun redisContainer(
    redisConnectionFactory: LettuceConnectionFactory,
    messageListener: MessageListener,
    topicSse: ChannelTopic,
  ): RedisMessageListenerContainer = RedisMessageListenerContainer().apply {
    setConnectionFactory(redisConnectionFactory)
    addMessageListener(messageListener, topicSse)
  }
}

interface PublisherSse {
  fun publish(userName: String, userId: Long)
}

@Service
internal class PublisherSseImpl(
  private var redisTemplate: RedisTemplate<String, Any>,
  private var topic: ChannelTopic,
) : PublisherSse {

  override fun publish(userName: String, userId: Long) {
    redisTemplate.convertAndSend(topic.topic, "${userName}$SEPARATOR${userId}")
  }
}

@Service
internal class SubscriberSseImpl(private val feedSse: FeedSse) :
  MessageListener {

  override fun onMessage(message: Message, pattern: ByteArray?) {
    val (name, id) = String(message.body).split(SEPARATOR)
    feedSse.sendToEmitters(name, id.toLong())
  }
}
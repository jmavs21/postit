package com.posts.api.conf

import com.posts.api.web.sse.SseFeedService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.stereotype.Service

@Configuration
class MessageQueueConfig {

  @Bean
  fun sseTopic(): ChannelTopic {
    return ChannelTopic("sse:queue")
  }

  @Bean
  fun messagePublisher(
    redisTemplate: RedisTemplate<String, Any>,
    sseTopic: ChannelTopic,
  ): MessagePublisher = RedisMessagePublisher(redisTemplate, sseTopic)

  @Bean
  fun messageListenerAdapter(sseFeedService: SseFeedService): MessageListenerAdapter =
    MessageListenerAdapter(RedisMessageSubscriber(sseFeedService))

  @Bean
  fun redisContainer(
    redisConnectionFactory: LettuceConnectionFactory,
    messageListenerAdapter: MessageListenerAdapter,
    sseTopic: ChannelTopic,
  ): RedisMessageListenerContainer = RedisMessageListenerContainer().apply {
    setConnectionFactory(redisConnectionFactory)
    addMessageListener(messageListenerAdapter, sseTopic)
  }
}

interface MessagePublisher {
  fun publish(message: String)
}

@Service
class RedisMessagePublisher(
  private var redisTemplate: RedisTemplate<String, Any>,
  private var topic: ChannelTopic,
) : MessagePublisher {

  override fun publish(message: String) {
    redisTemplate.convertAndSend(topic.topic, message)
  }
}

@Service
class RedisMessageSubscriber(private val sseFeedService: SseFeedService) : MessageListener {

  override fun onMessage(message: Message, pattern: ByteArray?) {
    val body = String(message.body)
    println("***** Message received body: $body")
    val parts = body.split("|")
    sseFeedService.feedNotify(parts[0], parts[1].toLong())
  }
}
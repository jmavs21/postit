package com.posts.api.web.sse

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Service
internal class FeedSseImpl(private val emitters: MutableMap<Long, SseEmitter> = ConcurrentHashMap()) :
  FeedSse {

  override fun addEmitter(userId: Long, emitter: SseEmitter) {
    emitters[userId] = emitter
  }

  override fun removeEmitter(userId: Long) {
    emitters.remove(userId)
  }

  @Async
  override fun sendToEmitters(userName: String, skipUserId: Long) {
    val deadEmitters = mutableListOf<Long>()
    for ((userId, emitter) in emitters) {
      try {
        if (userId == skipUserId) continue
        emitter.send("New post from $userName")
      } catch (e: Exception) {
        deadEmitters.add(userId)
      }
    }
    for (id in deadEmitters) {
      emitters.remove(id)
    }
  }
}
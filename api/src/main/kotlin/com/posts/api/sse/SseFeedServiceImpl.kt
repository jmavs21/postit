package com.posts.api.sse

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Service
internal class SseFeedServiceImpl(private val emitters: MutableMap<Long, SseEmitter> = ConcurrentHashMap()) :
  SseFeedService {

  override fun addEmitter(id: Long, emitter: SseEmitter) {
    emitters[id] = emitter
  }

  override fun removeEmitter(id: Long) {
    emitters.remove(id)
  }

  @Async
  override fun feedNotify(userName: String, exceptFromId: Long) {
    val deadEmitters = mutableListOf<Long>()
    for ((id, emitter) in emitters) {
      try {
        if (id == exceptFromId) continue
        emitter.send("New post from $userName")
      } catch (e: Exception) {
        deadEmitters.add(id)
      }
    }
    for (id in deadEmitters) {
      emitters.remove(id)
    }
  }
}
package com.posts.api.web.sse

import com.posts.api.follows.FollowService
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Service
@EnableAsync
internal class FeedSseImpl(
  private val emitters: MutableMap<Long, SseEmitter> = ConcurrentHashMap(),
  private val followService: FollowService
) :
  FeedSse {

  override fun addEmitter(userId: Long, emitter: SseEmitter) {
    emitters[userId] = emitter
  }

  override fun removeEmitter(userId: Long) {
    emitters.remove(userId)
  }

  @Async
  override fun sendToEmitters(userName: String, toUserId: Long) {
    val deadEmitters = mutableListOf<Long>()
    val followers = followService.findFollowers(toUserId).map { it.from.id }.toSet()
    for ((userId, emitter) in emitters) {
      try {
        if (userId in followers) emitter.send("New post from $userName")
      } catch (e: Exception) {
        deadEmitters.add(userId)
      }
    }
    for (id in deadEmitters) {
      emitters.remove(id)
    }
  }
}
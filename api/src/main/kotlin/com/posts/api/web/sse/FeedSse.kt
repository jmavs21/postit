package com.posts.api.web.sse

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

interface FeedSse {

  fun addEmitter(userId: Long, emitter: SseEmitter)

  fun removeEmitter(userId: Long)

  fun sendToEmitters(userName: String, toUserId: Long)
}
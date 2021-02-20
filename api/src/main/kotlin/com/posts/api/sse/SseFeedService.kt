package com.posts.api.sse

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

interface SseFeedService {

  fun addEmitter(id: Long, emitter: SseEmitter)

  fun removeEmitter(id: Long)

  fun feedNotify(userName: String, exceptFromId: Long)
}
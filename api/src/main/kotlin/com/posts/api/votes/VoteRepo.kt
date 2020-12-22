package com.posts.api.votes

import org.springframework.data.repository.CrudRepository

interface VoteRepo : CrudRepository<Vote, VoteId> {
  fun findAllByUserId(userId: Long): List<Vote>
}
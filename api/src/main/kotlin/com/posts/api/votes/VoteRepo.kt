package com.posts.api.votes

import org.springframework.data.repository.CrudRepository

internal interface VoteRepo : CrudRepository<Vote, VoteId> {

  fun findAllByUserId(userId: Long): List<Vote>
}
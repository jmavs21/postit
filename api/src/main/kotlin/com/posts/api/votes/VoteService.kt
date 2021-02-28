package com.posts.api.votes

import com.posts.api.posts.Post
import com.posts.api.users.User

interface VoteService {

  fun findAllByUserId(userId: Long): List<Vote>

  fun findByIdOrNull(voteId: VoteId): Vote?

  fun create(isUpVote: Boolean, post: Post, user: User): Int
}
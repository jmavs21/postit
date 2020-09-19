package com.postit.postitserver.repo

import com.postit.postitserver.model.Post
import com.postit.postitserver.model.User
import com.postit.postitserver.model.UserPostVote
import com.postit.postitserver.model.Vote
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.LocalDateTime

interface UserRepo : CrudRepository<User, Long> {
  fun findOneByEmail(email: String): User?
}

interface PostRepo : PagingAndSortingRepository<Post, Long> {
  fun findTop11ByCreatedatLessThanOrderByCreatedatDesc(createdat: LocalDateTime): List<Post>
}

interface VoteRepo : CrudRepository<Vote, UserPostVote> {
  fun findAllByUserId(userId: Long): List<Vote>
}
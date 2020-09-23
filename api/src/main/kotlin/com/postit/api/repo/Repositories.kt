package com.postit.api.repo

import com.postit.api.model.Post
import com.postit.api.model.User
import com.postit.api.model.UserPostVote
import com.postit.api.model.Vote
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface UserRepo : CrudRepository<User, Long> {
  fun findOneByEmail(email: String): User?
}

interface PostRepo : PagingAndSortingRepository<Post, Long> {
  // fun findTop11ByCreatedatLessThanOrderByCreatedatDesc(createdat: LocalDateTime): List<Post>

//  @Query(nativeQuery = true, value = "SELECT p.*, u.* FROM posts p JOIN users u ON p.user_id = u.id WHERE p.createdat < :createdat ORDER BY p.createdat DESC LIMIT :limit")
//  fun findPostsFeed(@Param("createdat") createdat: LocalDateTime, limit: Int): List<Post>
   @Query(value = "SELECT p, u FROM Post p JOIN User u ON p.user.id = u.id WHERE p.createdat < :createdat ORDER BY p.createdat DESC")
   fun findPostsFeed(@Param("createdat") createdat: LocalDateTime, pageable: Pageable): List<Post>
}

interface VoteRepo : CrudRepository<Vote, UserPostVote> {
  fun findAllByUserId(userId: Long): List<Vote>
}
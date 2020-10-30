package com.posts.api.repo

import com.posts.api.model.*
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
  @Query(value = """
    SELECT p, u 
    FROM Post p 
    JOIN User u 
      ON p.user.id = u.id 
    WHERE p.createdat < :createdat
    ORDER BY p.createdat DESC
  """)
  fun findPostsFeed(@Param("createdat") createdat: LocalDateTime, pageable: Pageable): List<Post>

  @Query(value = """
    SELECT p, u 
    FROM Post p 
    JOIN User u 
      ON p.user.id = u.id 
    WHERE p.createdat < :createdat AND (
        LOWER(p.title) LIKE LOWER('%'||:search||'%') OR 
        LOWER(p.text)  LIKE LOWER('%'||:search||'%') OR
        LOWER(u.name)  LIKE LOWER('%'||:search||'%')
      ) 
    ORDER BY p.createdat DESC
  """)
  fun findPostsFeedSearch(
    @Param("createdat") createdat: LocalDateTime,
    @Param("search") search: String,
    pageable: Pageable,
  ): List<Post>
}

interface VoteRepo : CrudRepository<Vote, VoteId> {
  fun findAllByUserId(userId: Long): List<Vote>
}

interface FollowRepo : CrudRepository<Follow, FollowId> {
  @Query(value = """
    SELECT f, u
    FROM Follow f
    JOIN User u
      ON f.to.id = u.id
    WHERE f.from.id = :fromId
  """)
  fun findAllByFromId(fromId: Long): List<Follow>

  @Query(value = """
    SELECT f, u
    FROM Follow f
    JOIN User u
      ON f.from.id = u.id
    WHERE f.to.id = :toId
  """)
  fun findAllByToId(toId: Long): List<Follow>
}
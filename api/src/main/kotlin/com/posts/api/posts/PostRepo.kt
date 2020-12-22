package com.posts.api.posts

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface PostRepo : PagingAndSortingRepository<Post, Long> {
  @Query(value = """
    SELECT p, u 
    FROM Post p 
    JOIN User u 
      ON p.user.id = u.id 
    WHERE p.createdat < :createdat
    ORDER BY p.createdat DESC
  """)
  fun findPosts(@Param("createdat") createdat: LocalDateTime, pageable: Pageable): List<Post>

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
  fun findPostsSearch(
    @Param("createdat") createdat: LocalDateTime,
    @Param("search") search: String,
    pageable: Pageable,
  ): List<Post>
}
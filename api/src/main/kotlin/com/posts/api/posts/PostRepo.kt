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
    WHERE p.createdate < :createdate
    ORDER BY p.createdate DESC
  """)
  fun findPosts(@Param("createdate") createdate: LocalDateTime, pageable: Pageable): List<Post>

  @Query(value = """
    SELECT p, u 
    FROM Post p 
    JOIN User u 
      ON p.user.id = u.id 
    WHERE p.createdate < :createdate AND (
        LOWER(p.title) LIKE LOWER('%'||:search||'%') OR 
        LOWER(p.text)  LIKE LOWER('%'||:search||'%') OR
        LOWER(u.name)  LIKE LOWER('%'||:search||'%')
      ) 
    ORDER BY p.createdate DESC
  """)
  fun findPostsSearch(
    @Param("createdate") createdate: LocalDateTime,
    @Param("search") search: String,
    pageable: Pageable,
  ): List<Post>
}
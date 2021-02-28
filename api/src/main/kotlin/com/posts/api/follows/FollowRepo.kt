package com.posts.api.follows

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

internal interface FollowRepo : CrudRepository<Follow, FollowId> {

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
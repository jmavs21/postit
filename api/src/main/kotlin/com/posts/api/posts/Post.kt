package com.posts.api.posts

import com.posts.api.users.User
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "posts")
class Post(
  @Column(nullable = false)
  var title: String,

  @Column(nullable = false, columnDefinition = "TEXT")
  var text: String,

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  var user: User,

  @Column(nullable = false)
  var points: Int = 0,

  @Column(nullable = false)
  var createdat: LocalDateTime = LocalDateTime.now(),

  @Column(nullable = false)
  var updatedat: LocalDateTime = LocalDateTime.now(),

  @Column(nullable = false, updatable = false)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0,

  @Transient
  var voteValue: Int = 0,

  @Transient
  var isFollow: Boolean = false,
)

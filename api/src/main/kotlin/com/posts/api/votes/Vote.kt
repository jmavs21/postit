package com.posts.api.votes

import com.posts.api.posts.Post
import com.posts.api.users.User
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "votes")
class Vote(
  @Column(nullable = false)
  var value: Int = 0,

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  var user: User,

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @MapsId("postId")
  @JoinColumn(name = "post_id")
  var post: Post,

  @EmbeddedId
  val id: VoteId,
)

@Embeddable
data class VoteId(var userId: Long, var postId: Long) : Serializable
package com.posts.api.follows

import com.posts.api.users.User
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "follows")
class Follow(
  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @MapsId("fromId")
  @JoinColumn(name = "from_id")
  var from: User,

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @MapsId("toId")
  @JoinColumn(name = "to_id")
  var to: User,

  @EmbeddedId
  val id: FollowId,
)

@Embeddable
data class FollowId(var fromId: Long, var toId: Long) : Serializable
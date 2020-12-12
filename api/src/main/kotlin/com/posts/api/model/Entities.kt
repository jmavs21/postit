package com.posts.api.model

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable
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

@Entity
@Table(name = "users")
class User : UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  val id: Long = 0

  @Column(nullable = false)
  var name: String = ""

  @Column(nullable = false)
  private var password: String = ""

  @Column(unique = true, nullable = false)
  var email: String = ""

  @Column(nullable = false)
  var createdat: LocalDateTime = LocalDateTime.now()

  @Column(nullable = false)
  var updatedat: LocalDateTime = LocalDateTime.now()

  override fun getAuthorities(): Collection<GrantedAuthority?> = ArrayList()

  override fun isEnabled(): Boolean = true

  override fun getUsername(): String = email

  override fun getPassword(): String = password

  override fun isCredentialsNonExpired(): Boolean = true

  override fun isAccountNonExpired(): Boolean = true

  override fun isAccountNonLocked(): Boolean = true

  fun setPassword(password: String) {
    this.password = password
  }
}
package com.postit.postitserver.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "posts")
class Post(
    @Column(nullable = false) var title: String,
    @Column(nullable = false) var createdat: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false) var updatedat: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false, updatable = false) @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0)

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

  override fun getAuthorities(): Collection<GrantedAuthority?>? {
    return ArrayList()
  }

  override fun isEnabled(): Boolean {
    return true
  }

  override fun getUsername(): String {
    return email
  }

  override fun getPassword(): String {
    return password
  }

  override fun isCredentialsNonExpired(): Boolean {
    return true
  }

  override fun isAccountNonExpired(): Boolean {
    return true
  }

  override fun isAccountNonLocked(): Boolean {
    return true
  }

  fun setPassword(password: String) {
    this.password = password
  }
}
package com.posts.api.users

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "users")
class User : UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  var id: Long = 0

  @Column(nullable = false)
  var name: String = ""

  @Column(nullable = false)
  private var password: String = ""

  @Column(unique = true, nullable = false)
  var email: String = ""

  @Column(nullable = false)
  var createdate: LocalDateTime = LocalDateTime.now()

  @Column(nullable = false)
  var updatedate: LocalDateTime = LocalDateTime.now()

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
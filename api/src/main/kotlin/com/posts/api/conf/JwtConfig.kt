package com.posts.api.conf

import com.posts.api.users.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.security.Key
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val X_AUTH_TOKEN = "x-auth-token"

/**
 * This filter gets executed for any incoming request.
 * The filter checks if the request has a JWT token in its headers and if the token is valid then
 * authenticates the user.
 */
@Component
class JwtRequestFilter(
  val myUserDetailsService: MyUserDetailsService,
  val jwtTokenUtil: JwtTokenUtil,
) : OncePerRequestFilter() {

  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain,
  ) {
    val jwtToken = request.getHeader(X_AUTH_TOKEN)
    val username = getValidUsernameFromToken(jwtToken)
    if (jwtToken != null && username != null) authenticateUser(request, jwtToken, username)
    filterChain.doFilter(request, response)
  }

  private fun getValidUsernameFromToken(jwtToken: String?): String? {
    if (jwtToken != null && jwtToken.contains(".")) {
      try {
        return jwtTokenUtil.getUsernameFromToken(jwtToken)
      } catch (e: IllegalArgumentException) {
        logger.error("Unable to get JWT Token.")
      } catch (e: ExpiredJwtException) {
        logger.error("JWT Token has expired.")
      }
    }
    return null
  }

  private fun authenticateUser(request: HttpServletRequest, jwtToken: String, username: String) {
    if (SecurityContextHolder.getContext().authentication == null) {
      val userDetails = myUserDetailsService.loadUserByUsername(username)
      if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
        val usernamePasswordAuthenticationToken =
          UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities).apply {
            details = WebAuthenticationDetailsSource().buildDetails(request)
          }
        SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
      }
    }
  }
}

/**
 * Creates and validates JSON Web Tokens by using jsonwebtoken library.
 */
@Component
class JwtTokenUtil(
  @Value("\${jwt.secret}")
  val jwtSecret: String,

  @Value("\${jwt.expiration.millis}")
  val jwtExpirationMillis: String,
) {

  fun getUsernameFromToken(token: String): String = getAllClaimsFromToken(token).subject

  fun generateToken(user: User): String =
    Jwts.builder().setClaims(getClaims(user)).setSubject(user.email)
      .setIssuedAt(Date(System.currentTimeMillis()))
      .setExpiration(Date(System.currentTimeMillis() + jwtExpirationMillis.toLong()))
      .signWith(getSigningKey()).compact()

  fun validateToken(token: String, userDetails: UserDetails): Boolean =
    getUsernameFromToken(token) == userDetails.username && !isTokenExpired(token)

  private fun getAllClaimsFromToken(token: String?): Claims =
    Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).body

  private fun getClaims(user: User): Map<String, Any> =
    hashMapOf("id" to user.id, "name" to user.name, "email" to user.email)

  private fun isTokenExpired(token: String): Boolean =
    getAllClaimsFromToken(token).expiration.before(Date())

  private fun getSigningKey(): Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret))
}
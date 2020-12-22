package com.posts.api.conf

import com.posts.api.error.ErrorFieldException
import com.posts.api.users.User
import com.posts.api.users.UserRepo
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.security.Key
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val X_AUTH_TOKE = "x-auth-token"

/**
 * WebSecurity and HttpSecurity configuration.
 * Including AuthenticationManager, PasswordEncoder and CORS.
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
  val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
  val jwtUserDetailsService: JwtUserDetailsService,
  val jwtRequestFilter: JwtRequestFilter,
) : WebSecurityConfigurerAdapter() {

  @Autowired
  @Throws(Exception::class)
  fun configureGlobal(auth: AuthenticationManagerBuilder) {
    auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder())
  }

  @Throws(Exception::class)
  override fun configure(httpSecurity: HttpSecurity) {
    httpSecurity
      .cors().and().csrf().disable()
      .headers().frameOptions().deny().and().authorizeRequests()
      .antMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
      .antMatchers("/api/auth", "/api/users").permitAll()
      .anyRequest().authenticated().and().exceptionHandling()
      .authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
  }

  @Throws(Exception::class)
  override fun configure(web: WebSecurity) {
    web.ignoring().antMatchers(
      "/v2/api-docs", "/configuration/ui", "/swagger-resources/**",
      "/configuration/security", "/swagger-ui.html", "/webjars/**", "/h2/**"
    )
  }

  @Bean
  fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

  @Bean
  fun corsConfigurationSource(): CorsConfigurationSource? {
    val config = CorsConfiguration().apply {
      allowedOrigins = listOf("*")
      allowedMethods = listOf("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH")
      allowedHeaders = listOf("*")
    }
    return UrlBasedCorsConfigurationSource().apply {
      registerCorsConfiguration("/**", config)
    }
  }
}

/**
 * Rejects every unauthenticated request and sends an 401 error code.
 */
@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {
  @Throws(IOException::class, ServletException::class)
  override fun commence(
    request: HttpServletRequest?, response: HttpServletResponse,
    authException: AuthenticationException?,
  ) = response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
}


/**
 * This filter gets executed for any incoming request.
 * The filter checks if the request has a JWT token in its headers and if the token is valid then
 * authenticates the user.
 */
@Component
class JwtRequestFilter(
  val jwtUserDetailsService: JwtUserDetailsService,
  val jwtTokenUtil: JwtTokenUtil,
) : OncePerRequestFilter() {

  @Throws(ServletException::class, IOException::class)
  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain,
  ) {
    val jwtToken = request.getHeader(X_AUTH_TOKE)
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
      val userDetails = jwtUserDetailsService.loadUserByUsername(username)
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
 * AuthenticationManager will use this method to fetch the user.
 */
@Service
class JwtUserDetailsService(val userRepo: UserRepo) : UserDetailsService {
  @Throws(UsernameNotFoundException::class)
  override fun loadUserByUsername(email: String): UserDetails = userRepo.findOneByEmail(email)
    ?: throw ErrorFieldException(hashMapOf("email" to "the email doesn't exists"),
      HttpStatus.BAD_REQUEST)
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
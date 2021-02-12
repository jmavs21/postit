package com.posts.api.conf

import com.posts.api.cache.UserCache
import com.posts.api.cache.UserCacheRepo
import com.posts.api.users.User
import com.posts.api.users.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * WebSecurity and HttpSecurity configuration.
 * Including UserDetailsService, AuthenticationManager, JwtRequestFilter and CORS.
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
  val myAuthenticationEntryPoint: MyAuthenticationEntryPoint,
  val myUserDetailsService: MyUserDetailsService,
  val jwtRequestFilter: JwtRequestFilter,
) : WebSecurityConfigurerAdapter() {

  @Bean
  fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

  @Autowired
  fun configureGlobal(auth: AuthenticationManagerBuilder) {
    auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder())
  }

  override fun configure(httpSecurity: HttpSecurity) {
    httpSecurity
      .cors().and().csrf().disable()
      .headers().frameOptions().deny().and().authorizeRequests()
      .antMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
      .antMatchers("/api/auth", "/api/users").permitAll()
      .anyRequest().authenticated().and().exceptionHandling()
      .authenticationEntryPoint(myAuthenticationEntryPoint).and()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
  }

  override fun configure(web: WebSecurity) {
    web.ignoring().antMatchers(
      "/v2/api-docs", "/configuration/ui", "/swagger-resources/**",
      "/configuration/security", "/swagger-ui.html", "/webjars/**", "/h2/**"
    )
  }

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
class MyAuthenticationEntryPoint : AuthenticationEntryPoint {

  override fun commence(
    request: HttpServletRequest?, response: HttpServletResponse,
    authException: AuthenticationException?,
  ) = response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
}

/**
 * AuthenticationManager will use this method to fetch the user from cache otherwise from store.
 */
@Service
class MyUserDetailsService(val userService: UserService, val userCacheRepo: UserCacheRepo) :
  UserDetailsService {

  override fun loadUserByUsername(email: String): UserDetails {
    val userCache = userCacheRepo.findByIdOrNull(email)
    if (userCache != null) return User().apply {
      this.email = userCache.email
      this.name = userCache.name
      this.id = userCache.id
    }
    val user = userService.getUserByEmail(email)
    userCacheRepo.save(UserCache(user.email, user.name, user.id))
    return user
  }
}
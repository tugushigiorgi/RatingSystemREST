package com.leverx.RatingSystemRest.Infrastructure.Security;

import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Security configuration for the application using Spring Security.
 *
 * <p>This class sets up the authentication provider, JWT filter, CORS filter,
 * and HTTP security settings for route protection.</p>
 */
@EnableWebSecurity
@AllArgsConstructor
@Configuration
public class SecurityConfig {

  private final JwtFactory jwtFactory;
  private final UserRepository userRepository;

  /**
   * Configures the password encoder to use BCrypt hashing algorithm.
   *
   * @return a BCryptPasswordEncoder instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Configures the main security filter chain, including JWT authentication,
   * session management, and access rules.
   *
   * @param http                   the HTTP security builder
   * @param authenticationProvider the configured authentication provider
   * @param jwtFilter              the JWT authentication filter
   * @param corsFilter             the CORS filter
   * @return the configured {@link SecurityFilterChain}
   * @throws Exception in case of configuration errors
   */
  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      AuthenticationProvider authenticationProvider,
      JwtFilter jwtFilter,
      CorsFilter corsFilter
  ) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers("/api/auth/**", "/api/main/**", "/images/**").permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(corsFilter, CorsFilter.class)
        .build();
  }

  /**
   * Configures the CORS filter to allow requests from specified origins.
   *
   * @return the configured {@link CorsFilter}
   */
  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin("http://localhost:3000");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return new CorsFilter(source);
  }

  /**
   * Configures the authentication provider to use the user details service and password encoder.
   *
   * @return a {@link DaoAuthenticationProvider} instance
   */
  @Bean
  public DaoAuthenticationProvider AuthenticationProvider() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userDetailsService());
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    return authenticationProvider;
  }

  /**
   * Defines the custom {@link UserDetailsService} that loads users from the database by email.
   *
   * @return the configured user details service
   */
  @Bean
  public UserDetailsService userDetailsService() {
    return username -> userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
  }

  /**
   * Provides the authentication manager from the Spring security configuration.
   *
   * @param config the authentication configuration
   * @return the configured {@link AuthenticationManager}
   * @throws Exception in case of configuration errors
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
}

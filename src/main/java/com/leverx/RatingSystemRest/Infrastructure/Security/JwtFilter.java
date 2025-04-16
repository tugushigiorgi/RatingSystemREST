package com.leverx.RatingSystemRest.Infrastructure.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that processes JWT authentication on each request.
 *
 * <p>This filter checks for the presence of a Bearer token in the `Authorization` header,
 * validates it using {@link JwtFactory}, and sets the Spring Security context accordingly
 * if the token is valid.</p>
 */
@Service
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final UserDetailsService userDetailsService;
  private final JwtFactory jwtfactory;

  /**
   * Intercepts incoming HTTP requests to perform JWT authentication.
   *
   * <p>If the request contains a valid Bearer token, it extracts the user information,
   * validates the token, and sets the user authentication in the Spring Security context.</p>
   *
   * @param request     the incoming {@link HttpServletRequest}
   * @param response    the outgoing {@link HttpServletResponse}
   * @param filterChain the filter chain to pass the request/response to the next filter
   * @throws ServletException if the filtering fails
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String userEmail;

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }


    jwt = authHeader.substring(7);
    userEmail = jwtfactory.extractUsername(jwt);

    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

      if (jwtfactory.isTokenValid(jwt, userDetails)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    filterChain.doFilter(request, response);
  }
}

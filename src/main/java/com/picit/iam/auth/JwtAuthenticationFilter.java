package com.picit.iam.auth;

import com.picit.iam.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final RequestAttributeSecurityContextRepository requestAttributeSecurityContextRepository = new RequestAttributeSecurityContextRepository();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String AUTH_HEADER = "Authorization";
        final String authHeader = request.getHeader(AUTH_HEADER);

        String AUTH_PREFIX = "Bearer ";
        if (authHeader == null || !authHeader.startsWith(AUTH_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = authHeader.substring(7);
            final String userName = jwtUtil.extractUsername(token);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userName != null && authentication == null) {
                UserDetails userDetails = userRepository.findByUsername(userName);

                if (jwtUtil.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    var context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authToken);
                    this.requestAttributeSecurityContextRepository.saveContext(context, request, response);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
            handlerExceptionResolver.resolveException(request, response, null, e);
            return;
        }

        filterChain.doFilter(request, response);
    }

}

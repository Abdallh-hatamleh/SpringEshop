package com.orange.eshop.cart_service.config.security;



import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer "))
        {
            log.warn("missing token");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }


        try {
        final String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            String userName = jwtService.extractUserName(token);
            Long userId = jwtService.extractUserId(token);

            if(username == null || !jwtService.isTokenValid(token,username)){
                log.warn("Invalid token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    new CustomPrincipal(userId,userName,username),
                    null,
                    null
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request,response);
        } catch (Exception e) {
            log.error("Error validating request to {} : {} ",request.getRequestURI() ,e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}

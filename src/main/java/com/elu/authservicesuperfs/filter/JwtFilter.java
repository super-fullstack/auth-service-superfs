package com.elu.authservicesuperfs.filter;


import com.elu.authservicesuperfs.service.AppUserDetailsService;
import com.elu.authservicesuperfs.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private AppUserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil,  AppUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    private static final List<String> PUBLIC_URLS =
            List.of(
                    "/auth/login",
                    "/auth/signup",
                    "/auth/test",
                    "/actuator/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/favicon.ico/**",
                    "/.well-known/**"
            );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws
            ServletException, IOException {
//        String path = request.getServletPath();
//
//        if (PUBLIC_URLS.contains(path)) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        String path = request.getRequestURI();
        System.out.println("THIS IS THE PATH: " + path);

        if (PUBLIC_URLS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
            System.out.println("THIS IS OKAYY " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = null;
        String email = null;

        // 1. check the auth header
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);

            System.out.println("THIS THE THE TOKE IN THE HEADER" + jwt);
        }

        // 2. if not found check cookie
        if (jwt == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        System.out.println("THIS THE THE TOKEN IN THE COOKIE" + jwt);
                        break;
                    }
                }
            }
        }

        // 3. validate the token and set the security context

        if  (jwt != null) {
            email = jwtUtil.extractEmail(jwt);
            System.out.println("THIS IS THE EMAIL EXTRACTED FORM THE TOKEN " + email);
            if (email != null
                    && SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    System.out.println("THE TOKE IS VALIDATED");
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }

        filterChain.doFilter(request,response);
    }
}

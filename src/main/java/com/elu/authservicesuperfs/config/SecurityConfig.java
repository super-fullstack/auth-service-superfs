package com.elu.authservicesuperfs.config;


import com.elu.authservicesuperfs.filter.JwtFilter;
import com.elu.authservicesuperfs.service.AppUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public CustomAuthenticationEntryPoint authenticationEntryPoint;

    public JwtFilter jwtFilter;

    public AppUserDetailsService appUserDetailsService;

    public SecurityConfig(
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint
            , JwtFilter jwtFilter
            , AppUserDetailsService appUserDetailsService) {
        this.authenticationEntryPoint = customAuthenticationEntryPoint;
        this.jwtFilter = jwtFilter;
        this.appUserDetailsService = appUserDetailsService;
    }


    @Bean
    public SecurityFilterChain
    securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers(
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/auth/signup",
                                        "/auth/test",
                                        "/auth/login",
                                        "/v3/api-docs/**",
                                        "/actuator/**",
                                        "/favicon.ico/**",
                                        "/.well-known/**",
                                        "/open-feign/**",
                                        "/signup", "/auth/api/open-feign/**").permitAll()
                                .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(authenticationEntryPoint));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        System.out.println("INSIDE AUTHENTICATIONMANAGER");
        DaoAuthenticationProvider authenticationProvider
                = new DaoAuthenticationProvider();
        authenticationProvider
                .setPasswordEncoder(passwordEncoder());
        authenticationProvider
                .setUserDetailsService(appUserDetailsService);
        return new ProviderManager(authenticationProvider);


    }

}

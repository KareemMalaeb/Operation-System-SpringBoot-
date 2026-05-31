package com.example.OperationSystem.config;

import com.example.OperationSystem.security.CustomUserDetailsService;
import com.example.OperationSystem.security.JwtFilter;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableAsync
@EnableCaching
public class AppConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtFilter jwtFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public AppConfig(CustomUserDetailsService customUserDetailsService, JwtFilter jwtFilter,
                     CorsConfigurationSource corsConfigurationSource) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtFilter = jwtFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    Jackson2ObjectMapperBuilderCustomizer jacksonEnumCoercion() {
        return builder -> builder.postConfigurer(mapper ->
            mapper.coercionConfigFor(Enum.class)
                  .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull)
        );
    }

    @Bean
    FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtFilter jwtFilter) {
        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>(jwtFilter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/public/**", "/api/auth/login", "/api/enums").permitAll()
                .requestMatchers("/api/auth/register").hasRole("MANAGER")
                
                .requestMatchers(HttpMethod.GET, "/api/users", "/api/users/**").hasAnyRole("SALES", "MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/users").hasRole("MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("MANAGER")
                
                .requestMatchers(HttpMethod.POST, "/api/inquiries").hasAnyRole("SALES", "OPERATOR", "MANAGER")
                .requestMatchers(HttpMethod.GET, "/api/inquiries").hasAnyRole("SALES", "OPERATOR", "MANAGER")
                .requestMatchers("/api/inquiries/{id}/assign").hasAnyRole("SALES", "OPERATOR", "MANAGER")
                .requestMatchers("/api/inquiries/{id}/agents/send").hasAnyRole("OPERATOR", "MANAGER")
                .requestMatchers(HttpMethod.GET,  "/api/inquiries/*/quotations").hasAnyRole("SALES", "OPERATOR", "MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/inquiries/*/quotations").hasAnyRole("OPERATOR", "MANAGER")
                .requestMatchers("/api/inquiries/*/quotations/select").hasAnyRole("OPERATOR", "MANAGER")
                .requestMatchers("/api/inquiries/*/send-to-client").hasAnyRole("OPERATOR", "MANAGER")

                .requestMatchers("/api/agents/**").hasAnyRole("OPERATOR", "MANAGER")

                .requestMatchers("/api/invoices/**").hasAnyRole("SALES", "OPERATOR", "MANAGER")
                .requestMatchers("/api/reports/**").hasAnyRole("SALES", "OPERATOR", "MANAGER")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
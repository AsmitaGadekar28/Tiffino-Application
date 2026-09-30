package com.tiffino.tiffino.config;
/**
 import com.tiffino.tiffino.service.CustomUserDetailsService;
 import lombok.RequiredArgsConstructor;
 import org.springframework.context.annotation.*;
 import org.springframework.security.authentication.AuthenticationManager;
 import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
 import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
 import org.springframework.security.config.http.SessionCreationPolicy;
 import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 import org.springframework.security.crypto.password.PasswordEncoder;
 import org.springframework.security.web.SecurityFilterChain;
 import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
 import org.springframework.security.config.annotation.web.builders.HttpSecurity;

 @Configuration
 @RequiredArgsConstructor
 public class SecurityConfiguration {

 private final JwtAuthenticationFilter jwtAuthenticationFilter;
 private final CustomUserDetailsService customUserDetailsService;

 @Bean
 public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
 http
 .csrf(csrf -> csrf.disable())
 .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
 .authorizeHttpRequests(auth -> auth
 .requestMatchers("/auth/**").permitAll()
 .requestMatchers("/manager/updatePassword","/manager/forgotPassword", "/manager/changePassword").permitAll()
 .requestMatchers("/deliveryPerson/updatePassword").permitAll()
 .requestMatchers("/superadmin/**").hasRole("SUPER_ADMIN")
 .requestMatchers("/manager/**").hasRole("MANAGER") // Move this above any /** matcher
 .requestMatchers("/user/**").hasRole("USER")
 .requestMatchers("/deliveryPerson/**").hasRole("DELIVERY_PERSON")

 .anyRequest().authenticated()
 )
 .authenticationProvider(authenticationProvider())
 .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

 return http.build();
 }

 @Bean
 public DaoAuthenticationProvider authenticationProvider() {
 DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
 provider.setUserDetailsService(customUserDetailsService);
 provider.setPasswordEncoder(passwordEncoder());
 return provider;
 }

 @Bean
 public PasswordEncoder passwordEncoder() {
 return new BCryptPasswordEncoder();
 }

 @Bean
 public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
 return config.getAuthenticationManager();
 }
 }

 **/

import com.tiffino.tiffino.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ✅ Enable CORS globally
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ✅ Disable CSRF for REST APIs
                .csrf(csrf -> csrf.disable())

                // ✅ No session creation
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ Authorization configuration
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (Auth, Public APIs)
                        .requestMatchers(
                                "/auth/**",
                                "/api/password/**",
                                "/user/getAllAvailableMealsWithCuisine",
                                "/user/getAllMealsByStateName/**",
                                "/user/getOffers",
                                "/user/getAllCuisines",
                                "/user/getAllCloudkitchennames",
                                "/user/getAllStateName",
                                "/user/searchFilterForUser/**"
                        ).permitAll()

                        // ✅ Allow chatbot + WebSocket endpoints
                        .requestMatchers("/api/chatbot/**", "/ws/**", "/ws", "/topic/**", "/app/**").permitAll()

                        // ✅ Allow frontend static files
                        .requestMatchers(
                                "/",
                                "/chat.html",
                                "/manager.html",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/chatbot/**",
                                "/ai-chat.html",
                                "/api/ai/**"
                        ).permitAll()

                        // ✅ Allow OPTIONS requests (for CORS preflight)
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ Allow Swagger / API docs
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // Role-based security
                        .requestMatchers("/superadmin/**").hasRole("SUPER_ADMIN")
                        .requestMatchers("/manager/**").hasRole("MANAGER")
                        .requestMatchers("/deliveryPerson/**").hasRole("DELIVERY_PERSON")
                        .requestMatchers("/user/**").hasRole("USER")

                        // Any other request → needs authentication
                        .anyRequest().authenticated()
                )

                // ✅ Add authentication provider + JWT filter
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:8080",
                "http://127.0.0.1:8080",
                "http://localhost:5500"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

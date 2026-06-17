package com.wpoms.admin.utilities.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

        @Autowired
        private JwtFilter jwtFilter;

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .cors(cors->{})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ========== PUBLIC ENDPOINTS ==========
                        .requestMatchers(
                                "/api/login",
                                "/api/register",
                                "/api/admin/register-manufacturer",
                                "/api/vendor/register",
                                "/api/customer/register-customer")
                        .permitAll()

                        // ========== SWAGGER UI ENDPOINTS ==========
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**")
                        .permitAll()

                        // ========== CUSTOMER ENDPOINTS (Only CUSTOMER role) ==========
                        .requestMatchers(
                                "/api/customer/view-customer",
                                "/api/customer/update-customer")
                        .hasRole("CUSTOMER")

                        // ========== VENDOR ENDPOINTS (Only VENDOR role) ==========
                        .requestMatchers(
                                "/api/vendor/get",
                                "/api/vendor/edit",
                                "/api/vendor/create-staff",
                                "/api/vendor/staff-list")
                        .hasRole("VENDOR")

                        // ========== MANUFACTURER ENDPOINTS (Only MANUFACTURER role) ==========
                        .requestMatchers(
                                "/api/admin/manufacturer",
                                "/api/admin/update-manufacture",
                                "/api/admin/manufacturer/create-staff",
                                "/api/admin/manufacturer/staff-list")
                        .hasRole("MANUFACTURER")

                        // ========== MANUFACTURER PRODUCT ENDPOINTS (Only MANUFACTURER role) ==========
                        .requestMatchers(
                                "/api/manufacturer/create-product",
                                "/api/manufacturer/products",
                                "/api/manufacturer/product",
                                "/api/manufacturer/update-product")
                        .hasRole("MANUFACTURER")

                        // ========== ALL OTHER REQUESTS ==========
                        .anyRequest().denyAll())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
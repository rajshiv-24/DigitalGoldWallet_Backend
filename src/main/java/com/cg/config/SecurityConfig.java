package com.cg.config;

import com.cg.security.JwtAuthFilter;
import com.cg.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          JwtAuthFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // PUBLIC — Swagger
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // PUBLIC — Auth (login/register)
                        .requestMatchers("/api/auth/**").permitAll()

                        // PUBLIC — Registration flow (address needed before user)
                        .requestMatchers(HttpMethod.POST, "/api/addresses").permitAll()

                        // ADMIN — Admin namespace
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ADMIN — User management
                        .requestMatchers(HttpMethod.GET,    "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/users/count").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/users/by-email").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/users/*/exists").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*").hasRole("ADMIN")

                        // AUTHENTICATED — Own user profile
                        .requestMatchers(HttpMethod.GET,  "/api/users/*").authenticated()
                        .requestMatchers(HttpMethod.PUT,  "/api/users/*").authenticated()
                        .requestMatchers(HttpMethod.GET,  "/api/users/*/balance").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/users/wallet/top-up").authenticated()

                        // AUTHENTICATED — Gold transactions (own data only)
                        .requestMatchers(HttpMethod.POST, "/api/gold/buy").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/gold/sell").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/gold/convert-to-physical").authenticated()
                        .requestMatchers(HttpMethod.GET,  "/api/gold/holdings/by-user/*").authenticated()
                        .requestMatchers(HttpMethod.GET,  "/api/gold/holdings/by-user/*/total").authenticated()
                        .requestMatchers(HttpMethod.GET,  "/api/gold/history/by-user/*").authenticated()
                        .requestMatchers(HttpMethod.GET,  "/api/gold/physical/by-user/*").authenticated()
                        .requestMatchers(HttpMethod.GET,  "/api/gold/summary/by-user/*").authenticated()

                        // ADMIN — Gold aggregate views
                        .requestMatchers(HttpMethod.GET, "/api/gold/**").hasRole("ADMIN")

                        // AUTHENTICATED — Own payments
                        .requestMatchers(HttpMethod.GET, "/api/payments/by-user/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/payments/by-user/*/total").authenticated()

                        // ADMIN — All payments aggregate views
                        .requestMatchers(HttpMethod.GET, "/api/payments/**").hasRole("ADMIN")

                        // ADMIN — Vendor sensitive endpoints
                        .requestMatchers(HttpMethod.GET,    "/api/vendors/count").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/vendors/by-email").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/vendors/*/exists").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/vendors").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/vendors/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/vendors/*").hasRole("ADMIN")

                        // AUTHENTICATED — Vendor browsing
                        .requestMatchers(HttpMethod.GET, "/api/vendors/by-name").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/vendors").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/vendors/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/vendors/*/price").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/vendors/*/branches").authenticated()

                        // ADMIN — Remaining vendor wildcard fallback
                        .requestMatchers(HttpMethod.GET, "/api/vendors/**").hasRole("ADMIN")

                        // ADMIN — Branch sensitive endpoints
                        .requestMatchers(HttpMethod.GET,    "/api/branches/count").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/branches/*/exists").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/branches").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/branches/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/branches/*").hasRole("ADMIN")

                        // AUTHENTICATED — Branch browsing
                        .requestMatchers(HttpMethod.GET, "/api/branches/by-vendor/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/branches/by-city").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/branches/with-min-quantity").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/branches").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/branches/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/branches/*/stock").authenticated()

                        // ADMIN — Remaining branch wildcard fallback
                        .requestMatchers(HttpMethod.GET, "/api/branches/**").hasRole("ADMIN")

                        // ADMIN/USER — Addresses
                        .requestMatchers(HttpMethod.GET, "/api/addresses/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT,    "/api/addresses/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/addresses/*").hasRole("ADMIN")

                        // ADMIN — Metadata system count
                        .requestMatchers(HttpMethod.GET, "/api/metadata/endpoints/count").hasRole("ADMIN")

                        // AUTHENTICATED — Metadata enums (needed for forms)
                        .requestMatchers(HttpMethod.GET, "/api/metadata/**").authenticated()

                        // Fallback
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )

                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests from localhost on any port (for local development)
        // Add your frontend URL here if you build a frontend later
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*"
        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

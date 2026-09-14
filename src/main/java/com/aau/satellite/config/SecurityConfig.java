package com.aau.satellite.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/login",
                        "/app.css",
                        "/favicon.ico",
                        "/error",
                        "/api/telemetry")
                    .permitAll()

                    // Administrative endpoints are restricted to ADMIN.
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")

                    // Only ADMIN and ENGINEER may view or change
                    // telemetry configuration.
                    .requestMatchers(HttpMethod.GET, "/configuration/**")
                    .hasAnyRole("ADMIN", "ENGINEER")
                    .requestMatchers(HttpMethod.POST, "/configuration/**")
                    .hasAnyRole("ADMIN", "ENGINEER")

                    // Satellite lifecycle changes are operational actions.
                    .requestMatchers(
                        HttpMethod.POST,
                        "/satellites/*/state",
                        "/api/satellites/*/state")
                    .hasAnyRole("ADMIN", "OPERATOR")

                    // Alert acknowledgement and resolution are operational actions.
                    .requestMatchers(
                        HttpMethod.POST,
                        "/alerts/*/acknowledge",
                        "/alerts/*/resolve")
                    .hasAnyRole("ADMIN", "OPERATOR")

                    // Mission creation and state transitions are operational actions.
                    .requestMatchers(
                        HttpMethod.POST,
                        "/missions",
                        "/missions/*/state",
                        "/api/missions",
                        "/api/missions/*/state")
                    .hasAnyRole("ADMIN", "OPERATOR")

                    // Commands may be issued by ADMIN, ENGINEER or OPERATOR.
                    .requestMatchers(
                        HttpMethod.POST,
                        "/commands",
                        "/api/commands")
                    .hasAnyRole("ADMIN", "ENGINEER", "OPERATOR")

                    // Read-only resources are available to every
                    // authenticated role, including VIEWER.
                    .requestMatchers(
                        HttpMethod.GET,
                        "/",
                        "/dashboard",
                        "/satellites",
                        "/satellites/**",
                        "/alerts",
                        "/missions",
                        "/commands",
                        "/audit",
                        "/api/satellites/**",
                        "/api/missions",
                        "/api/commands",
                        "/api/telemetry/**")
                    .authenticated()

                    // Any endpoint not explicitly assigned a permission
                    // is denied by default.
                    .anyRequest()
                    .denyAll())
        .formLogin(
            login ->
                login
                    .loginPage("/login")
                    .defaultSuccessUrl("/dashboard", true)
                    .failureUrl("/login?error")
                    .permitAll())
        .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
        .csrf(csrf -> csrf.ignoringRequestMatchers("/api/telemetry"));

    return http.build();
  }
}
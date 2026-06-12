package com.json.AutoAlquiler.config;

import com.json.AutoAlquiler.services.CustomUserDetailsService;

import java.util.Set;
import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider =
            new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            if (roles.toString().toUpperCase().contains("ADMIN")) {
                response.sendRedirect("/vehicles/list");
            } else {
                response.sendRedirect("/catalog");
            }
        };
    }    

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth ->
                auth
                    // 1. Bloqueo absoluto de creación y edición (Tanto ver el formulario GET como procesarlo POST)
                    .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll() // Permite forwards internos del sistema
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                    // 2. Rutas públicas
                    .requestMatchers("/auth/**", "/", "/error").permitAll()
                    .requestMatchers(HttpMethod.GET, "/catalog").permitAll()
                    // 3. Permisos compartidos entre Cliente y Administrador
                    .requestMatchers("/catalog/**", "/reservations/**").hasAnyRole("CLIENT", "ADMIN", "SUPER_ADMIN")
                    .requestMatchers("/vehicles/details/**").hasAnyRole("CLIENT", "ADMIN", "SUPER_ADMIN")
                    .requestMatchers("/locations/**").hasAnyRole("CLIENT", "ADMIN", "SUPER_ADMIN")
                    // 4. Permisos exclusivos para el Administrador
                    .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .requestMatchers("/vehicles/list/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .requestMatchers("/vehicles/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .requestMatchers("/reservations/delete/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                    // 5 cualquier otra ruta solicita autenticar (log in)
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .successHandler(customAuthenticationSuccessHandler())
                .permitAll()
            )
            .logout(logout ->
                logout
                    .logoutUrl("/auth/logout")
                    .logoutSuccessUrl("/auth/login?logout")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .permitAll()
            )
            .exceptionHandling(
                exception -> exception.accessDeniedPage("/403") // Si ocurre una excepción debido a algun intento de ingreso custionable se redirige a la página 403 - Prohibido
            );

        return http.build();
    }
}

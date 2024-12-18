package com.fitness.config;

import com.fitness.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserService userService;
    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(@Lazy UserService userService, CustomAuthenticationSuccessHandler successHandler) {
        this.userService = userService;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/web/auth/**").permitAll() // Доступ к страницам регистрации/логина

                // Ограничение доступа к управлению пользователями только для ADMIN
                .requestMatchers("/web/users/**").hasRole("ADMIN")

                // Доступ к различным разделам в зависимости от ролей
                .requestMatchers("/web/admin/**").hasRole("ADMIN")
                .requestMatchers("/web/trainer/**").hasAnyRole("ADMIN", "TRAINER")
                .requestMatchers("/web/client/**").hasAnyRole("ADMIN", "CLIENT")

                // Общая главная страница доступна для всех аутентифицированных пользователей
                .requestMatchers("/web/home").authenticated()

                // Остальные веб-страницы требуют аутентификации
                .requestMatchers("/web/**").authenticated()

                // API-endpoints с разными ролями
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/clients/**").hasAnyRole("ADMIN", "CLIENT")
                .requestMatchers("/api/trainers/**").hasAnyRole("ADMIN", "TRAINER")
                .requestMatchers("/api/classes/**").hasAnyRole("ADMIN", "TRAINER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/web/auth/login")
                .successHandler(successHandler) // Используем кастомный successHandler
                .failureUrl("/web/auth/login?error=true")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/web/auth/logout")
                .logoutSuccessUrl("/web/auth/login?logout=true")
                .permitAll();

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

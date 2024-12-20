package com.fitness.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    private final AuthenticationSuccessHandler successHandler;

    public SecurityConfig(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Отключаем CSRF для REST API, можно оставить включенным для веб-части
                .csrf().disable()

                // Настройка авторизации
                .authorizeHttpRequests()
                .requestMatchers("/error").permitAll() // Разрешаем доступ ко всем страницам с ошибками
                .requestMatchers("/api/auth/**").permitAll() // REST API для аутентификации
                .requestMatchers("/web/auth/**").permitAll() // Веб-страницы аутентификации
                .requestMatchers("/web/users/**").hasRole("ADMIN") // Управление пользователями через веб
                .requestMatchers("/web/admin/**").hasRole("ADMIN") // Админские веб-страницы
                .requestMatchers("/web/trainer/**").hasAnyRole("ADMIN", "TRAINER") // Доступ для тренеров
                .requestMatchers("/web/client/**").hasAnyRole("ADMIN", "CLIENT") // Доступ для клиентов
                .requestMatchers("/web/home").authenticated() // Доступ к домашней странице
                .requestMatchers("/web/**").authenticated() // Защита всех страниц
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Админские API
                .requestMatchers("/api/users/**").hasRole("ADMIN") // API для управления пользователями
                .requestMatchers("/api/clients/**").hasAnyRole("ADMIN", "CLIENT") // API для клиентов
                .requestMatchers("/api/trainers/**").hasAnyRole("ADMIN", "TRAINER") // API для тренеров
                .requestMatchers("/api/classes/**").hasAnyRole("ADMIN", "TRAINER") // API для фитнес-классов
                .anyRequest().authenticated() // Любые другие запросы требуют аутентификации

                // Настройка входа в систему
                .and()
                .formLogin()
                .loginPage("/web/auth/login") // Страница входа
                .successHandler(successHandler) // Обработчик успешного входа
                .failureUrl("/web/auth/login?error=true") // URL в случае ошибки входа
                .permitAll()

                // Настройка выхода из системы
                .and()
                .logout()
                .logoutUrl("/web/auth/logout") // URL выхода
                .logoutSuccessUrl("/web/auth/login?logout=true") // URL после успешного выхода
                .permitAll();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Использование BCrypt для хэширования паролей
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

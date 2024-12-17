package com.fitness.config;

import com.fitness.entity.Role;
import com.fitness.entity.User;
import com.fitness.service.RoleService;
import com.fitness.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleService roleService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleService roleService, UserService userService, PasswordEncoder passwordEncoder) {
        this.roleService = roleService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Создание ролей, если они не существуют
        if (!roleService.findByName("ROLE_ADMIN").isPresent()) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleService.save(adminRole);
        }
        if (!roleService.findByName("ROLE_CLIENT").isPresent()) {
            Role clientRole = new Role();
            clientRole.setName("ROLE_CLIENT");
            roleService.save(clientRole);
        }
        if (!roleService.findByName("ROLE_TRAINER").isPresent()) {
            Role trainerRole = new Role();
            trainerRole.setName("ROLE_TRAINER");
            roleService.save(trainerRole);
        }

        // Создание администратора, если не существует
        if (!userService.getAllUsers().stream().anyMatch(u -> u.getUsername().equals("admin"))) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin")); // Зашифрованный пароль
            admin.setEmail("admin@fitness.com");
            userService.registerNewUser(admin, Set.of("ROLE_ADMIN"));
            System.out.println("Admin user created with username 'admin' and password 'admin'");
        }
    }
}

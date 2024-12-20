package com.fitness.service;

import com.fitness.entity.Role;
import com.fitness.entity.User;
import com.fitness.repository.ClientRepository;
import com.fitness.repository.TrainerRepository;
import com.fitness.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final TrainerRepository trainerRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder, TrainerRepository trainerRepository, ClientRepository clientRepository) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;

        this.trainerRepository = trainerRepository;
        this.clientRepository = clientRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }


    public List<User> getAllByRole(String roleName) {
        return userRepository.findAllByRoles_Name(roleName);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword); // passwordEncoder настроен в SecurityConfig
    }




    // Метод для регистрации нового пользователя
    public User registerNewUser(User user, Set<String> roleNames) {
        Set<Role> roles = roleNames.stream()
                .map(roleName -> roleService.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }



    public List<User> findAllByRole(String roleName) {
        return userRepository.findAllByRoles_Name(roleName);
    }

    public void deleteByUsername(String username) {
        userRepository.findByUsername(username).ifPresent(userRepository::delete);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean isPasswordValid(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public User updateUser(User user, Set<String> roleNames) {
        // Преобразование Set<String> в Set<Role>
        Set<Role> roles = roleNames.stream()
                .map(roleService::findByName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        // Установить новые роли
        user.setRoles(roles);

        // Обновить связанные данные, если пользователь является клиентом или тренером
        clientRepository.findByUser(user).ifPresent(client -> {
            client.setFirstName(user.getFirstName());
            client.setLastName(user.getLastName());
            clientRepository.save(client);
        });

        trainerRepository.findByUser(user).ifPresent(trainer -> {
            trainer.setFirstName(user.getFirstName());
            trainer.setLastName(user.getLastName());
            trainerRepository.save(trainer);
        });

        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    // Метод для поиска пользователя по ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }



    // Реализация метода из UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
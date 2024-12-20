package com.fitness.service;

import com.fitness.entity.Role;
import com.fitness.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> findByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    public Set<Role> findRolesByNames(Set<String> roleNames) {
        return roleNames.stream()
                .map(roleRepository::findByName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    // Получение всех ролей
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public void deleteByName(String name) {
        roleRepository.deleteByName(name);
    }



    public List<Role> findAll() {
        return roleRepository.findAll();
    }


}

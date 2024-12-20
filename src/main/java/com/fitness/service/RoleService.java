package com.fitness.service;

import com.fitness.entity.Role;
import com.fitness.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> findByName(String roleName) {
        return roleRepository.findByName(roleName);
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

package ru.kata.spring.boot_security.demo.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public List<User> showAllUsers() {
        return userRepository.findAllWithRoles();
    }

    @Override
    @Transactional
    public User getUser(Long id) {
        return userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public void createUser(User user, List<Long> rolesId) {
        if (rolesId == null || rolesId.isEmpty()) {
            throw new IllegalArgumentException("User must have at least one role");
        }

        Set<Role> roles = roleRepository.findAllByIdIn(rolesId);
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("No valid roles found");
        }

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }


    @Transactional
    public void updateUser(Long id, User updatedUser, List<Long> roleIds) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Проверка уникальности email
        if (!user.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("Email уже существует");
            }
        }

        // Обновление пароля (только если не пустой)
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // Обновление ролей (только если переданы roleIds)
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> newRoles = roleRepository.findByIds(roleIds);
            if (newRoles.isEmpty()) {
                throw new IllegalArgumentException("Роли не найдены");
            }
            user.setRoles(newRoles);
        }

        // Обновление остальных полей
        user.setName(updatedUser.getName());
        user.setSurname(updatedUser.getSurname());
        user.setAge(updatedUser.getAge());
        user.setEmail(updatedUser.getEmail());
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByUsername(String email) {
        return userRepository.findByUsernameWithRoles(email);
    }
}

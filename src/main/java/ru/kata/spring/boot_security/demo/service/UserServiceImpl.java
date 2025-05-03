package ru.kata.spring.boot_security.demo.service;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;
import java.util.Set;

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
    public List<User> showAllUsers() {
        return userRepository.findAll();
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

    @Override
    @Transactional
    public void updateUser(Long id, User updateUser, List<Long> roleIds) {

        User existingUser = getUser(id);

        existingUser.setUsername(updateUser.getUsername());

        String rawPassword = updateUser.getPassword();
        if (!rawPassword.isEmpty()
                && !passwordEncoder.matches(rawPassword, existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(rawPassword));
        }

        Set<Role> newRoles = roleRepository.findAllByIdIn(roleIds);
        if (newRoles.isEmpty()) {
            throw new IllegalArgumentException("User must have at least one role");
        }
        existingUser.setRoles(newRoles);

        existingUser.setName(updateUser.getName());
        existingUser.setSurname(updateUser.getSurname());
        existingUser.setAge(updateUser.getAge());

        userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsernameWithRoles(username);
    }
}

package ru.kata.spring.boot_security.demo.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
    public Optional<User> getUser(Long id) {

        return userRepository.findByIdWithRoles(id);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public User createUser(User user) {
        // Кодируем пароль перед сохранением
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Обрабатываем роли если они предоставлены
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<Role> existingRoles = new HashSet<>();
            for (Role role : user.getRoles()) {
                Role existingRole = roleRepository.findById(role.getId()).orElse(null);
                if (existingRole != null) {
                    existingRoles.add(existingRole);
                }
            }
            user.setRoles(existingRoles);
        }

        userRepository.save(user);
        return userRepository.findByIdWithRoles(user.getId()).orElse(user);

    }


    @Transactional
    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!user.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("Email уже существует");
            }
        }

        user.setName(updatedUser.getName());
        user.setSurname(updatedUser.getSurname());
        user.setAge(updatedUser.getAge());
        user.setEmail(updatedUser.getEmail());


        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        user.getRoles().clear();
        if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
            for (Role role : updatedUser.getRoles()) {
                Role existingRole = roleRepository.findById(role.getId()).orElse(null);
                if (existingRole != null) {
                    user.getRoles().add(existingRole);
                }
            }
        }


        userRepository.save(user);
        return userRepository.findByIdWithRoles(user.getId()).orElse(user);

    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getByUsername(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public String formatRoles(Collection<Role> roles) {
        return roles.stream().map(role -> role.getName()
                        .replace("ROLE_", " "))
                .collect(Collectors.joining());
    }
}






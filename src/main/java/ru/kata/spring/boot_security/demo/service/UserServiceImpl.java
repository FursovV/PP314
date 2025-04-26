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

    @Transactional
    @Override
    public List<User> showAllUsers() {
        List<User> users = userRepository.findAll();
        return users;
    }

    @Transactional
    @Override
    public User getUser(Long id) {
        User userById = userRepository.getOne(id);
        return userById;
    }

    @Transactional
    @Override
    public void createUser(User user, List<Long> rolesId) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = (Set<Role>) roleRepository.findByIdIn(rolesId);
        user.getRoles().addAll(roles);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updateUser(Long id, User updateUser) {
        User userToBeUpdated = userRepository.getOne(id);
        userToBeUpdated.setName(updateUser.getName());
        userToBeUpdated.setSurname(updateUser.getSurname());
        userToBeUpdated.setAge(updateUser.getAge());
        userToBeUpdated.setUsername(updateUser.getUsername());
        userToBeUpdated.setPassword(passwordEncoder.encode(updateUser.getPassword()));
        userRepository.save(userToBeUpdated);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

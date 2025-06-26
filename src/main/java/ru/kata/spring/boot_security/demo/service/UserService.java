package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> showAllUsers();

    Optional<User> getUser(Long id);

    List<Role> getAllRoles();

    User createUser(User user);

    User updateUser(Long id, User updateUser);

    void deleteUser(Long id);

    Optional<User> findById(Long id);

    User getByEmail(String email);

    User getByUsername(String username);

    String formatRoles(Collection<Role> roles);
}

package ru.kata.spring.boot_security.demo.service;


import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {

    List<User> showAllUsers();

    User getUser(Long id);

    List<Role> getAllRoles();

    void createUser(User user, List<Long> rolesId);

    void updateUser(Long id, User updateUser, List<Long> roleId);

    void deleteUser(Long id);

    User findByUsername(String username);
}

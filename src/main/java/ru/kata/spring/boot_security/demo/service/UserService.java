package ru.kata.spring.boot_security.demo.service;



import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {
    List<User> showAllUsers();

    User getUser(Long id);

    void createUser(User user, List<Long> rolesId);

    void updateUser(Long id, User updateUser);

    void deleteUser(Long id);

    User findByUsername(String username);
}

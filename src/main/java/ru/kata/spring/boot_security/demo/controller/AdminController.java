package ru.kata.spring.boot_security.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.exception.CreateEx;
import ru.kata.spring.boot_security.demo.exception.NoSuchUserException;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.Map;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/loadUser")
    public ResponseEntity<User> getLoadUser(Authentication authentication) {
        String username = authentication.getName();
        User loadUser = userService.getByEmail(username);
        if (loadUser != null) {
            loadUser.setFormattedRoles(userService.formatRoles(loadUser.getRoles()));
            return new ResponseEntity<>(loadUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> showAllUsers() {
        List<User> users = userService.showAllUsers();
        users.forEach(user -> user.setFormattedRoles(userService.formatRoles(user.getRoles())));
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        Optional<User> userOptional = userService.getUser(id);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            User user = userOptional.get();
            user.setFormattedRoles(userService.formatRoles(user.getRoles()));
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (userService.getByEmail(user.getEmail()) != null) {
            throw new CreateEx("такой email существует");
        }
        User savedUser = userService.createUser(user);
        savedUser.setFormattedRoles(userService.formatRoles(savedUser.getRoles()));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        User user = userService.getUser(id)
                .orElseThrow(() -> new NoSuchUserException("Пользователя не существует с " + id));
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Пользователь успешно удалён"));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        userService.updateUser(id, user);
        User updated = userService.getUser(id).orElse(user);
        updated.setFormattedRoles(userService.formatRoles(updated.getRoles()));
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = userService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
}


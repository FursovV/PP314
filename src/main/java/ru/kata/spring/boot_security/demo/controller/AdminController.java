package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showAdminPanel(Model model) {
        model.addAttribute("users", userService.showAllUsers());
        model.addAttribute("user", new User()); // Для формы создания
        model.addAttribute("allRoles", userService.getAllRoles());
        return "list";
    }

    @GetMapping("/edit/{id}")
    public User getUserForEdit(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", userService.getAllRoles());
        return "create";
    }

    @PostMapping("/create")
    public String createUser(
            @ModelAttribute("user") @Valid User user,
            BindingResult bindingResult,
            @RequestParam("roles") List<Long> rolesId,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", userService.getAllRoles());
            return "create";
        }

        try {
            userService.createUser(user, rolesId);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", true); // Исправлено для работы с param.error
            model.addAttribute("allRoles", userService.getAllRoles());
            return "create";
        }

        return "redirect:/admin";
    }

    @PostMapping("/update")
    public String updateUser(
            @ModelAttribute("user") @Valid User user,
            BindingResult bindingResult,
            @RequestParam(value = "roles", required = false) List<Long> roleIds, // required = false
            Model model) {


        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.showAllUsers());
            model.addAttribute("allRoles", userService.getAllRoles());
            return "list";
        }

        try {
            userService.updateUser(user.getId(), user, roleIds);
            return "redirect:/admin";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при обновлении: " + e.getMessage());
        }

        model.addAttribute("users", userService.showAllUsers());
        model.addAttribute("allRoles", userService.getAllRoles());
        return "list";
    }

    @GetMapping("/user")
    public String userPage(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "users";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}
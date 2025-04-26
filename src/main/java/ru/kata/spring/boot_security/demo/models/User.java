package ru.kata.spring.boot_security.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "Поле не может быть пустым")
    @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z]+$", message = "Имя не может содержать символы отличные от букв")
    @Column
    private String name;

    @NotEmpty(message = "Поле не может быть пустым")
    @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z]+$", message = "Имя не может содержать символы отличные от букв")
    @Column
    private String surname;

    @NotNull(message = "Поле не может быть пустым")
    @Min(value = 0, message = "Возраст должен быть больше нуля")
    @Column
    private Integer age;

    @NotNull(message = "Поле не может быть пустым")
    @Column(unique = true)
    private String username;

    @NotNull(message = "Поле не может быть пустым")
    @Column
    private String password;

    @Column
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id",
                    referencedColumnName = "id"
            )
    )
    private Set<ru.kata.spring.boot_security.demo.model.Role> roles = new HashSet<>(); ;

    public User() {
    }

    public User(String name, String surname, Integer age) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return Objects.requireNonNullElse(age, 0);
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(Set<ru.kata.spring.boot_security.demo.model.Role> roles) {
        this.roles = roles;
    }

    public Set<ru.kata.spring.boot_security.demo.model.Role> getRoles() {
        return roles;
    }
}

package ru.kata.spring.boot_security.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.Collection;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT r FROM Role r WHERE r.id IN :ids")
    Set<Role> findByIdIn(@Param("ids") Collection<Long> ids);
    Set<Role> findAllByIdIn(@Param("ids") Collection<Long> ids);
    @Query("SELECT r FROM Role r WHERE r.id IN :ids")
    Set<Role> findByIds(@Param("ids") Collection<Long> ids);
}

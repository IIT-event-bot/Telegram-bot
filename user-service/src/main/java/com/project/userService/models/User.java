package com.project.userService.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
@Entity
public class User {
    @Id
    private long id;

    @Column(name = "username")
    private String username;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private RoleDto role;
}

package com.project.userService.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "statements")
@Entity
public class Statement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "patronymic", nullable = false)
    private String patronymic;

    @Column(name = "group_id", nullable = false)
    @JsonIgnore
    private long groupId;

    @Column(name = "is_checked", nullable = false)
    @JsonProperty("isChecked")
    private boolean isChecked;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Transient
    private String groupName;
}

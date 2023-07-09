package com.project.userService.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "session")
public class Session {
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "token")
    private String token;

    @Column(name = "expired_time")
    private LocalDateTime expiredTime;

    @Column(name = "session_time")
    private LocalDateTime sessionTime;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "is_expired")
    private boolean isExpired;
}

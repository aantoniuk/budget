package com.budget.core.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter @Setter
@NoArgsConstructor @RequiredArgsConstructor
@Entity
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    private String login;
    @NonNull
    private String password;
    private boolean enable = true;
}

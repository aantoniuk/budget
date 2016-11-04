package com.budget.core.entity;

import com.budget.core.Utils.OperationType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@EqualsAndHashCode(exclude = {"children"})
@NoArgsConstructor @RequiredArgsConstructor
@Entity
public class UserCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    private String name;
    @NonNull
    @Enumerated(EnumType.STRING)
    private OperationType type;
    private boolean enable = true;
    private Long parentId;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="parentId")
    private Set<UserCategory> children = new HashSet<>();
    @NonNull
    @Column(updatable = false)
    private long userId;
}

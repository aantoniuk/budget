package com.budget.core.entity;

import com.budget.core.Utils.OperationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@Entity
public class UserCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private OperationType type;
    private boolean enable = false;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserCategory parent;
    @OneToMany(mappedBy = "parent")
    private Set<UserCategory> children;
    @ManyToOne
    private User user;
}

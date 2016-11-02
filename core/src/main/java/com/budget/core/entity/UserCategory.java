package com.budget.core.entity;

import com.budget.core.Utils.OperationType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter @Setter
@EqualsAndHashCode(exclude = {"children"})
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
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserCategory parent;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<UserCategory> children;
    @NonNull
    @ManyToOne
    private User user;
}

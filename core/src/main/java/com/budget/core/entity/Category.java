package com.budget.core.entity;

import com.budget.core.Utils.OperationType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter @Setter
@Entity
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private OperationType type;
    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;
    @OneToMany(mappedBy = "parent")
    private Set<Category> children;
}

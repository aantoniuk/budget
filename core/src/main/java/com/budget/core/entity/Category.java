package com.budget.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter
public class Category {
    @Id @GeneratedValue
    private long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;
    @OneToMany(mappedBy = "parent")
    private Set<Category> children;
}

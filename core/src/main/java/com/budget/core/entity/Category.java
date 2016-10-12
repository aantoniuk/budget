package com.budget.core.entity;

import com.budget.core.Utils.OperationType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter @Setter
@EqualsAndHashCode(exclude = {"children"})
@Entity
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private OperationType type;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Category parent;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Category> children;
}

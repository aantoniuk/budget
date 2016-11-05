package com.budget.core.entity;

import com.budget.core.Utils.OperationType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@EqualsAndHashCode(callSuper = true, exclude = {"children"})
@NoArgsConstructor
@Entity
public class Category extends BaseCategory{
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="parentId")
    private Set<Category> children = new HashSet<>();

    public Category(String name, OperationType type) {
        super(name, type);
    }
}

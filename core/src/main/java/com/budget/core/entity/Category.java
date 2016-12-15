package com.budget.core.entity;

import com.budget.core.Utils.OperationType;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@EqualsAndHashCode(callSuper = true, exclude = {"children"})
@NoArgsConstructor @AllArgsConstructor()
@Entity
public class Category extends BaseCategory {
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="parentId")
    private Set<Category> children = new HashSet<>();

    @Builder
    public Category(String name, OperationType type, Boolean enable, Long parentId) {
        super(name, type, enable, parentId);
    }
}

package com.budget.core.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@EqualsAndHashCode(callSuper = true, exclude = {"children"})
@RequiredArgsConstructor
@Entity
public class Category extends BaseCategory{
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="parentId")
    private Set<Category> children = new HashSet<>();
}

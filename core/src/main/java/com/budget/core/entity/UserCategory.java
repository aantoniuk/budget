package com.budget.core.entity;

import com.budget.core.Utils.OperationType;
import lombok.*;
import lombok.experimental.NonFinal;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@EqualsAndHashCode(callSuper = true, exclude = {"children"})
@NoArgsConstructor
@Entity
public class UserCategory extends BaseCategory{
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="parentId")
    private Set<UserCategory> children = new HashSet<>();
    @NonNull
    @Column(updatable = false)
    private Long userId;

    public UserCategory(String name, OperationType type, @NonNull Long userId) {
        super(name, type);
        this.userId = userId;
    }
}

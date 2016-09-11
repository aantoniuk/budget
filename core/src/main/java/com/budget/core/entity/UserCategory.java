package com.budget.core.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter @Setter
@NoArgsConstructor
public class UserCategory extends Category {
    private boolean enable = false;
    @ManyToOne
    private User user;
}

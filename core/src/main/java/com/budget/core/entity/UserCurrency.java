package com.budget.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class UserCurrency {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private boolean enable = true;
    @ManyToOne
    private User user;
    @ManyToOne
    private Currency currency;
}

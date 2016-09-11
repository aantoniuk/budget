package com.budget.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter @Setter
public class UserCurrency {
    @Id @GeneratedValue
    private long id;
    private boolean enable = false;
    @ManyToOne
    private User user;
    @ManyToOne
    private Currency currency;
}

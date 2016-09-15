package com.budget.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Wallet {
    @Id @GeneratedValue
    private long id;
    private String name;
    private boolean enable;
    private UserCurrency userCurrency;
}

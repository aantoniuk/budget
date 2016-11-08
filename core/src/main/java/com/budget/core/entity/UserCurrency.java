package com.budget.core.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@EqualsAndHashCode
@Getter @Setter
public class UserCurrency {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Float value;
    private Boolean enable = true;
    @ManyToOne
    private User user;
    @ManyToOne
    private Currency currency;
}

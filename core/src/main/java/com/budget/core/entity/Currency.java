package com.budget.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Currency {
    @Id @GeneratedValue
    private int id;
    private String name;
    private long value;
}

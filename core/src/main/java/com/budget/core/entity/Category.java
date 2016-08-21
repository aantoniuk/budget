package com.budget.core.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

@Entity
@Data
public class Category {
    @GeneratedValue
    private long id;
    private String name;
    private long parentId;
}

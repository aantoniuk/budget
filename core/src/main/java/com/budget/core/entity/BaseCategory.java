package com.budget.core.entity;

import com.budget.core.Utils.OperationType;
import lombok.NonNull;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
abstract class BaseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;
    @NonNull
    protected String name;
    @NonNull
    @Enumerated(EnumType.STRING)
    protected OperationType type;
    protected boolean enable = true;
    protected Long parentId;
}

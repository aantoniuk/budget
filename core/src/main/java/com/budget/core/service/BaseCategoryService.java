package com.budget.core.service;

import com.budget.core.entity.BaseCategory;
import com.budget.core.entity.Wallet;

import java.util.stream.Stream;

public interface BaseCategoryService<T extends BaseCategory> extends AbstractService<T> {
    T create(T baseCategory);
}

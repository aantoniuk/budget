package com.budget.core.util;

import com.budget.core.entity.Category;

/**
 * Description:
 * Use it for building Category object simpler
 * -----------------------------------------------------
 * Created by kremezniy on 8/29/2016.
 */
public class CategoryBuilder {
    private Category category;

    public CategoryBuilder() {
        category = new Category();
    }

    public CategoryBuilder id(int id) {
        category.setId(id);
        return this;
    }

    public CategoryBuilder parent(Category category) {
        category.setParent(category);
        return this;
    }

    public CategoryBuilder name(String name) {
        category.setName(name);
        return this;
    }

    public Category build() {
        return category;
    }
}

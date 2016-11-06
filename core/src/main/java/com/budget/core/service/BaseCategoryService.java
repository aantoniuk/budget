package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.entity.BaseCategory;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

abstract class BaseCategoryService<T extends BaseCategory> extends AbstractService<T>{

    abstract void checkExistence(T t);

    abstract Stream<T> findByParentId(Long parentId);

    public Stream<T> findAllByParentId(@NonNull Long parentId) {
        Stream<T> subCategories = findByParentId(parentId);
        return Stream.concat(subCategories, subCategories.flatMap(t -> findAllByParentId(t.getParentId())));
    }

    @Transactional
    public T create(@NonNull T category) {
        if (category.getName() == null || category.getName().isEmpty()) {
            throw new NullPointerException("Name can't be empty");
        }
        // root category
        if (category.getParentId() == null) {
            if (category.getType() == null) {
                throw new NullPointerException("Type can't be empty");
            }
        } else {
            // sub category
            T parent = findParent(category.getParentId());
            // check type
            if (category.getType() == null) {
                category.setType(parent.getType());
            } else if (!category.getType().equals(parent.getType())) {
                throw new IllegalArgumentException("Category type doesn't suit to Parent Category type");
            }
            // check enable
            if (category.getEnable() == null) {
                category.setEnable(parent.getEnable());
            } else if (!category.getEnable().equals(parent.getEnable())) {
                throw new IllegalArgumentException("State of Category and Parent Category should be the same");
            }
        }
        checkExistence(category);
        return getDao().save(category);
    }

    @Transactional
    public T updateName(@NonNull Long id, @NonNull String name) {
        T category = find(id);

        category.setName(name);
        checkExistence(category);

        return getDao().save(category);
    }

    @Transactional
    public T updateType(@NonNull Long id, @NonNull OperationType type) {
        T category = find(id);

        // root category
        if(category.getParentId() == null) {
            category.setType(type);
            checkExistence(category);
            getDao().save(category);

            // update all children
            findAllByParentId(id).forEach(t -> {
                t.setType(type);
                getDao().save(t);
            });
            return category;
        }
        throw new UnsupportedOperationException("Sub Category can't be changed by type");
    }

    @Transactional
    public T updateEnable(@NonNull Long id, @NonNull Boolean enable) {
        T category = find(id);

        category.setEnable(enable);
        getDao().save(category);

        // update all children
        findAllByParentId(id).forEach(t -> {
            t.setEnable(enable);
            getDao().save(t);
        });
        return category;
    }

    @Transactional
    public T updateParent(@NonNull Long id, Long parentId) {
        T category = find(id);
        // nothing to update
        if(parentId == null && category.getParentId() == null ||
                parentId != null && parentId.equals(category.getParentId())) {
            return category;
        }
        if(parentId != null) {
            if(parentId.equals(category.getId())) {
                throw new IllegalArgumentException("Parent can't be itself");
            }
            // copy type and enable form parent
            T parent = findParent(parentId);
            category.setType(parent.getType());
            category.setEnable(parent.getEnable());

            findAllByParentId(id).forEach(t -> {
                t.setType(parent.getType());
                t.setEnable(parent.getEnable());
                getDao().save(t);
            });
        }
        category.setParentId(parentId);
        checkExistence(category);

        return getDao().save(category);
    }

    protected T find(@NonNull Long id) {
        Optional<T> categoryOpt = findOne(id);
        if(!categoryOpt.isPresent()) {
            throw new NullPointerException("Category isn't found by id=" + id);
        }
        return categoryOpt.get();
    }

    protected T findParent(@NonNull Long parentId) {
        T parent = getDao().findOne(parentId);
        if (parent == null) {
            throw new NullPointerException("Parent Category isn't found by id=" + parentId);
        }
        return parent;
    }
}

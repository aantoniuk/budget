package com.budget.core.service;

import com.budget.core.Utils.OperationType;
import com.budget.core.dao.CategoryDao;
import com.budget.core.entity.Category;
import com.budget.core.exception.ObjectNotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
public class CategoryServiceImpl extends BaseCategoryServiceImpl<Category> implements CategoryService {

    private final CategoryDao categoryDao;

    @Autowired
    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public Stream<Category> findByType(@NonNull OperationType type) throws ObjectNotFoundException {
        Supplier<Stream<Category>> supplier = () -> categoryDao.findByType(type);
        if (supplier.get().count() == 0) {
            throw new ObjectNotFoundException("Service: Objects Categories with type " + type + " has not been found for GETTING.");
        }
        return supplier.get();
    }

    public Stream<Category> findByParentId(Long parentId) {
        Supplier<Stream<Category>> supplier = () -> categoryDao.findByParentId(parentId);
        return supplier.get();
    }

    @Override
    void checkExistence(Category category) {
        Optional<Category> existedCategory = categoryDao.findByNameAndTypeAndParentId(category.getName(), category.getType(), category.getParentId());
        if (existedCategory.isPresent() && existedCategory.get().getId() != category.getId()) {
            String exMsg = String.format("Object already exists with name=%s, type=$s", category.getName(), category.getType().name());
            throw new IllegalArgumentException(exMsg);
    }
    }

    @Override
    CrudRepository<Category, Long> getDao() {
        return categoryDao;
    }
}

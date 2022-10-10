package ru.practicum.ExploreWithMe.service;

import ru.practicum.ExploreWithMe.model.Category;

import java.util.Collection;

public interface CategoryService {
    Category createCategory(Category category);

    Category updateCategory(Category category);

    void deleteCategory(long catId);

    Collection<Category> getAllCategories(int from, int size);

    Category getCategory(long catId);
}
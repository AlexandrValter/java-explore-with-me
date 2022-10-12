package ru.practicum.ExploreWithMe.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ExploreWithMe.model.Category;
import ru.practicum.ExploreWithMe.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/admin/categories")
    public Category createCategory(@RequestBody @Valid Category category) {
        return categoryService.createCategory(category);
    }

    @PatchMapping("admin/categories")
    public Category updateCategory(@RequestBody @Valid Category category) {
        return categoryService.updateCategory(category);
    }

    @DeleteMapping("admin/categories/{catId}")
    public void deleteCategory(@PathVariable long catId) {
        categoryService.deleteCategory(catId);
    }

    @GetMapping("/categories")
    public Collection<Category> getAllCategories(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "10") @Positive int size) {
        return categoryService.getAllCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public Category getCategory(@PathVariable long catId) {
        return categoryService.getCategory(catId);
    }
}
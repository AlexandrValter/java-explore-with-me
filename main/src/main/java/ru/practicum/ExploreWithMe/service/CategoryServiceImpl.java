package ru.practicum.ExploreWithMe.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ExploreWithMe.exception.CategoryNotFoundException;
import ru.practicum.ExploreWithMe.exception.DeleteCategoryException;
import ru.practicum.ExploreWithMe.model.Category;
import ru.practicum.ExploreWithMe.repository.CategoryRepository;
import ru.practicum.ExploreWithMe.repository.EventRepository;

import java.util.Collection;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final EventRepository eventRepository;

    public CategoryServiceImpl(CategoryRepository repository,
                               EventRepository eventRepository) {
        this.repository = repository;
        this.eventRepository = eventRepository;
    }

    @Override
    public Category createCategory(Category category) {
        log.info("Добавлена новая категория: {}", category.getName());
        return repository.save(category);
    }

    @Override
    public Category updateCategory(Category category) {
        if (repository.findById(category.getId()).isPresent()) {
            log.info("Обновлена информация о категории id={}", category.getId());
            return repository.save(category);
        } else {
            throw new CategoryNotFoundException(String.format("Category with id=%s was not found.", category.getId()));
        }
    }

    @Override
    public void deleteCategory(long catId) {
        if (repository.findById(catId).isPresent()) {
            if (eventRepository.getCountEventWithCategory(catId) == 0) {
                log.info("Удалена категория id={}", catId);
                repository.deleteById(catId);
            } else {
                throw new DeleteCategoryException(String.format(
                        "Some events are associated with the category id=%s", catId));
            }
        } else {
            throw new CategoryNotFoundException(String.format("Category with id=%s was not found.", catId));
        }
    }

    @Override
    public Collection<Category> getAllCategories(int from, int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        log.info("Запрошены категории в количестве {}", size);
        return repository.findAll(pageable).getContent();
    }

    @Override
    public Category getCategory(long catId) {
        log.info("Запрошена категория id={}", catId);
        return repository.findById(catId).orElseThrow(
                () -> new CategoryNotFoundException(
                        String.format("Category with id=%s was not found.", catId)));
    }
}
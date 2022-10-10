package ru.practicum.ExploreWithMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ExploreWithMe.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

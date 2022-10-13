package ru.practicum.ExploreWithMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ExploreWithMe.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}

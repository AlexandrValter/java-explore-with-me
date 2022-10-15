package ru.practicum.ExploreWithMe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ExploreWithMe.model.User;
import ru.practicum.ExploreWithMe.model.dto.UserDtoLikes;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select new ru.practicum.ExploreWithMe.model.dto.UserDtoLikes " +
            "(e.initiator.id, e.initiator.name, sum(size(e.likes))) " +
            "from Event e group by e.initiator.id, e.initiator.name " +
            "order by sum(e.likes.size) desc")
    Page<UserDtoLikes> findPopularUsers(Pageable pageable);
}

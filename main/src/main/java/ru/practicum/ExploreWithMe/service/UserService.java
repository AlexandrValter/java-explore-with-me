package ru.practicum.ExploreWithMe.service;

import ru.practicum.ExploreWithMe.model.User;
import ru.practicum.ExploreWithMe.model.dto.UserDtoLikes;

import java.util.Collection;

public interface UserService {
    User createUser(User user);

    Collection<User> getUsers(int[] ids, int from, int size);

    void deleteUser(long userId);

    Collection<UserDtoLikes> getPopularUsers(int from, int size);
}

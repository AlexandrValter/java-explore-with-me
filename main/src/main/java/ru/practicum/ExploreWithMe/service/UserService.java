package ru.practicum.ExploreWithMe.service;

import ru.practicum.ExploreWithMe.model.User;

import java.util.Collection;

public interface UserService {
    User createUser(User user);

    Collection<User> getUsers(int[] ids, int from, int size);

    void deleteUser(long userId);
}

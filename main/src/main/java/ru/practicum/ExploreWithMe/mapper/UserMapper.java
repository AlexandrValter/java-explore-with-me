package ru.practicum.ExploreWithMe.mapper;

import ru.practicum.ExploreWithMe.model.User;
import ru.practicum.ExploreWithMe.model.dto.UserDto;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName());
    }
}

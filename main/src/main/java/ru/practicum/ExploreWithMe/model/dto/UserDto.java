package ru.practicum.ExploreWithMe.model.dto;

import lombok.Data;

@Data
public class UserDto {
    private long id;
    private String name;

    public UserDto(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
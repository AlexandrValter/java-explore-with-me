package ru.practicum.ExploreWithMe.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoLikes {
    private long id;
    private String name;
    private long likes;
}
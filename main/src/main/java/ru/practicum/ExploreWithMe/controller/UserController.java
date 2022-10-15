package ru.practicum.ExploreWithMe.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ExploreWithMe.model.User;
import ru.practicum.ExploreWithMe.model.dto.UserDtoLikes;
import ru.practicum.ExploreWithMe.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/admin/users")
    public User createUser(@RequestBody @Valid User user) {
        return userService.createUser(user);
    }

    @GetMapping("/admin/users")
    public Collection<User> getUsers(
            @RequestParam(required = false) int[] ids,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/admin/users/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/users/popular")
    public Collection<UserDtoLikes> getPopularUsers(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "10") @Positive int size) {
        return userService.getPopularUsers(from, size);
    }
}
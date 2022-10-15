package ru.practicum.ExploreWithMe.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ExploreWithMe.exception.UserNotFoundException;
import ru.practicum.ExploreWithMe.model.User;
import ru.practicum.ExploreWithMe.model.dto.UserDtoLikes;
import ru.practicum.ExploreWithMe.repository.UserRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User createUser(User user) {
        log.info("Добавлен пользователь {}", user.getName());
        return userRepository.save(user);
    }

    @Override
    public Collection<User> getUsers(int[] ids, int from, int size) {
        if (ids == null) {
            int page = from / size;
            Pageable pageable = PageRequest.of(page, size);
            log.info("Запрошены пользователи в количестве {}", size);
            return userRepository.findAll(pageable).getContent();
        } else {
            log.info("Запрошены пользователи ids={}", ids);
            return Arrays.stream(ids)
                    .mapToObj((i) -> userRepository.findById((long) i)
                            .orElseThrow(() -> new UserNotFoundException(
                                    String.format("User with id=%s was not found.", i)
                            )))
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        if (userRepository.findById(userId).isPresent()) {
            log.info("Удален пользователь id={}", userId);
            userRepository.deleteById(userId);
        } else {
            throw new UserNotFoundException(String.format("User with id=%s was not found.", userId));
        }
    }

    @Override
    public Collection<UserDtoLikes> getPopularUsers(int from, int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        log.info("Запрошены популярные инициаторы событий");
        return userRepository.findPopularUsers(pageable).getContent();
    }
}
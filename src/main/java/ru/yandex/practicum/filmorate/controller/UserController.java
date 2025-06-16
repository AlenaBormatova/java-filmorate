package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) { // Создание пользователя
        validateUser(user);
        user.setId(idCounter++);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}.", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) { // Обновление пользователя
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с id {} не найден.", user.getId());
            throw new ValidationException("Пользователь с указанным id не найден.");
        }
        validateUser(user);
        users.put(user.getId(), user);
        log.info("Обновлён пользователь: {}.", user);
        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() { // Получение списка всех пользователей
        return users.values();
    }

    private void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            log.error("Логин содержит пробелы: {}.", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы.");
        }
    }
}
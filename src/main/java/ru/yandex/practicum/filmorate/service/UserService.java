package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) { // Создание пользователя
        validateUser(user); // Проверка валидности
        log.info("Добавлен новый пользователь: {}.", user);
        return userStorage.createUser(user); // Передача запроса в хранилище
    }

    public User updateUser(User user) { // Обновление пользователя
        validateUser(user);
        getUserById(user.getId()); // Проверка существования
        log.info("Обновлён пользователь: {}.", user);
        return userStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() { // Получение списка всех пользователей
        log.info("Получены все пользователи.");
        return userStorage.getAllUsers();
    }

    public User getUserById(int userId) { // Получение пользователя по ID
        log.info("Получен пользователь с id = {}.", userId);
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
    }

    public void addFriend(int userId, int friendId) { // Добавление в друзья
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        userStorage.addFriend(user.getId(), friend.getId());
        log.info("Пользователь с id = {} добавил в друзья пользователя с id = {}.", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) { // Удаление из друзей
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        userStorage.removeFriend(user.getId(), friend.getId());
        log.info("Пользователь с id = {} удалил из друзей пользователя с id = {}.", userId, friendId);
    }

    public Collection<User> getFriends(int userId) { // Получение списка друзей
        getUserById(userId); // Проверка существования
        log.info("Получен список друзей пользователя с id = {}.", userId);
        return userStorage.getFriends(userId);
    }

    public Collection<User> getCommonFriends(int userId, int otherId) { // Получение общих друзей
        getUserById(userId); // Проверка существования
        getUserById(otherId); // Проверка существования
        log.info("Получен список общих друзей пользователей с id = {} и с id = {}.", userId, otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }

    // Проверка, что логин не содержит пробелы
    private void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы.");
        }
    }
}
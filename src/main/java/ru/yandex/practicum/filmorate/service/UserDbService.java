package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDbService {
    private final UserStorage userStorage;
    private final FriendshipDao friendshipDao;

    public User createUser(User user) { // Создание пользователя
        validateUser(user); // Проверка валидности
        User createdUser = userStorage.createUser(user);
        log.info("Добавлен новый пользователь: {}", createdUser);
        return createdUser;
    }

    public User updateUser(User user) { // Обновление пользователя
        validateUser(user);
        getUserById(user.getId()); // Проверка существования
        User updatedUser = userStorage.updateUser(user);
        log.info("Обновлён пользователь: {}", updatedUser);
        return updatedUser;
    }

    public Collection<User> getAllUsers() { // Получение всех пользователей
        log.info("Получен список всех пользователей. Текущее количество: {}", userStorage.getAllUsers().size());
        return userStorage.getAllUsers();
    }

    public User getUserById(int userId) { // Получение пользователя по ID
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        log.info("Получен пользователь с id = {}", userId);
        return user;
    }

    public void addFriend(int userId, int friendId) { // Добавление в друзья
        getUserById(userId);
        getUserById(friendId);
        friendshipDao.addFriend(userId, friendId);
        log.info("Пользователь с id = {} добавил в друзья пользователя с id = {}", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) { // Удаление из друзей
        getUserById(userId);
        getUserById(friendId);
        friendshipDao.removeFriend(userId, friendId);
        log.info("Пользователь с id = {} удалил из друзей пользователя с id = {}", userId, friendId);
    }

    public Collection<User> getFriends(int userId) { // Получение списка друзей
        getUserById(userId);
        log.info("Получен список друзей пользователя с id = {}", userId);
        return friendshipDao.getFriends(userId);
    }

    public Collection<User> getCommonFriends(int userId, int otherId) { // Получение общих друзей
        getUserById(userId);
        getUserById(otherId);
        log.info("Получен список общих друзей пользователей с id = {} и {}", userId, otherId);
        return friendshipDao.getCommonFriends(userId, otherId);
    }

    private void validateUser(User user) { // Проверка, что логин не содержит пробелы
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
    }
}
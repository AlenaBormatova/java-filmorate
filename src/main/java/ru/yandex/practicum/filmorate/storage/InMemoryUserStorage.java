package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();
    private int idCounter = 1;

    @Override
    public User createUser(User user) { // Создание пользователя
        user.setId(idCounter++);
        users.put(user.getId(), user);
        friends.put(user.getId(), new HashSet<>());
        return user;
    }

    @Override
    public User updateUser(User user) { // Обновление пользователя
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() { // Получение списка всех пользователей
        return users.values();
    }

    @Override
    public Optional<User> getUserById(int userId) { // Получение пользователя по ID
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void addFriend(int userId, int friendId) { // Добавление в друзья
        friends.getOrDefault(userId, new HashSet<>()).add(friendId);
        friends.getOrDefault(friendId, new HashSet<>()).add(userId);
    }

    @Override
    public void removeFriend(int userId, int friendId) { // Удаление из друзей
        friends.getOrDefault(userId, new HashSet<>()).remove(friendId);
        friends.getOrDefault(friendId, new HashSet<>()).remove(userId);
    }

    @Override
    public Collection<User> getFriends(int userId) { // Получение списка друзей
        return friends.getOrDefault(userId, Collections.emptySet()).stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getCommonFriends(int userId, int otherId) { // Получение общих друзей
        Set<Integer> userFriends = friends.getOrDefault(userId, Collections.emptySet());
        Set<Integer> otherFriends = friends.getOrDefault(otherId, Collections.emptySet());

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
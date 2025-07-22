package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.util.Collection;

@Component
public class FriendshipDaoImpl implements FriendshipDao {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    public FriendshipDaoImpl(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        // Проверяем, есть ли уже запрос в друзья
        String checkSql = "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, friendId, userId);

        if (count > 0) {
            // Если есть встречный запрос, обновляем статус на "подтвержденный" для обоих
            String updateSql = "UPDATE friendships SET status = 'confirmed' " +
                    "WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
            jdbcTemplate.update(updateSql, userId, friendId, friendId, userId);
        } else {
            // Если нет, создаем новую запись со статусом "неподтвержденный"
            String insertSql = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, 'unconfirmed')";
            jdbcTemplate.update(insertSql, userId, friendId);
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);

        // Обновляем статус встречной дружбы, если она была подтверждена
        String updateSql = "UPDATE friendships SET status = 'unconfirmed' " +
                "WHERE user_id = ? AND friend_id = ? AND status = 'confirmed'";
        jdbcTemplate.update(updateSql, friendId, userId);
    }

    @Override
    public Collection<User> getFriends(int userId) {
        String sql = "SELECT u.* FROM users u JOIN friendships f ON u.user_id = f.friend_id WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, userMapper, userId);
    }

    @Override
    public Collection<User> getCommonFriends(int userId, int otherId) {
        String sql = "SELECT u.* FROM users u JOIN friendships f1 ON u.user_id = f1.friend_id " +
                "JOIN friendships f2 ON u.user_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(sql, userMapper, userId, otherId);
    }
}
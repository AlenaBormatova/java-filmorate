package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface LikeDao {
    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Collection<User> getLikes(int filmId);

    int getLikesCount(int filmId);

    Collection<Film> getPopularFilms(int count);
}
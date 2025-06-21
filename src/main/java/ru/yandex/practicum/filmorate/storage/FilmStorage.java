package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> getAllFilms();

    Optional<Film> getFilmById(int id);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Collection<Film> getPopular(int count);
}
package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreDao {
    Collection<Genre> getAllGenres();

    Optional<Genre> getGenreById(int id);

    Collection<Genre> getGenresByFilmId(int filmId); // Получение жанров для определённого фильма

    void addGenreToFilm(int filmId, int genreId);

    void removeGenresFromFilm(int filmId);
}
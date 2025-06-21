package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();
    private int idCounter = 1;

    @Override
    public Film createFilm(Film film) { // Добавление фильма
        film.setId(idCounter++);
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        return film;
    }

    @Override
    public Film updateFilm(Film film) { // Обновление фильма
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() { // Получение всех фильмов
        return films.values();
    }

    @Override
    public Optional<Film> getFilmById(int id) { // Получение фильма по ID
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void addLike(int filmId, int userId) { // Добавление лайка
        likes.getOrDefault(filmId, new HashSet<>()).add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) { // Удаление лайка
        likes.getOrDefault(filmId, new HashSet<>()).remove(userId);
    }

    @Override
    public Collection<Film> getPopular(int count) { // Получение популярных фильмов
        return films.values().stream()
                .sorted(Comparator.comparingInt(
                        film -> -1 * likes.getOrDefault(film.getId(), Collections.emptySet()).size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
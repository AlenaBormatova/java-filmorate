package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 1;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) { // Добавление фильма
        validateFilm(film);
        film.setId(idCounter++);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}.", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) { // Обновление фильма
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id {} не найден.", film.getId());
            throw new ValidationException("Фильм с указанным id не найден.");
        }
        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Обновлён фильм: {}.", film);
        return film;
    }

    @GetMapping
    public Collection<Film> getAllFilms() { // Получение всех фильмов
        return films.values();
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза раньше 28 декабря 1895 года.");
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
        }
    }
}
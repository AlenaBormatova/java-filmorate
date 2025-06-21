package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film createFilm(Film film) { // Добавление фильма
        validateFilm(film); // Проверка валидности
        log.info("Добавлен новый фильм: {}.", film);
        return filmStorage.createFilm(film); // Передача запроса в хранилище
    }

    public Film updateFilm(Film film) { // Обновление фильма
        validateFilm(film);
        getFilmById(film.getId()); // Проверка существования
        log.info("Обновлён фильм: {}.", film);
        return filmStorage.updateFilm(film);
    }

    public Collection<Film> getAllFilms() { // Получение всех фильмов
        log.info("Получены все фильмы.");
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int filmId) { // Получение фильма по ID
        log.info("Получен фильм с id = {}.", filmId);
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден."));
    }

    public void addLike(int filmId, int userId) { // Добавление лайка
        userService.getUserById(userId); // Проверка существования пользователя
        Film film = getFilmById(filmId); // Проверка существования фильма
        filmStorage.addLike(film.getId(), userId);
        log.info("Пользователь с id = {} поставил лайк фильму с id = {}.", userId, filmId);
    }

    public void removeLike(int filmId, int userId) { // Удаление лайка
        userService.getUserById(userId);
        Film film = getFilmById(filmId);
        filmStorage.removeLike(film.getId(), userId);
        log.info("Пользователь с id = {} убрал лайк у фильма с id = {}.", userId, filmId);
    }

    public Collection<Film> getPopular(int count) { // Получение популярных фильмов
        log.info("Получено {} популярных фильмов.", count);
        return filmStorage.getPopular(count);
    }

    // Проверка, что дата релиза не раньше первого в истории фильма
    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза раньше 28 декабря 1895 года.");
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
        }
    }
}
package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.LikeDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmDbService {
    private final FilmStorage filmStorage;
    private final UserDbService userService;
    private final GenreDao genreDao;
    private final LikeDao likeDao;
    private final MpaDao mpaDao;

    @Transactional
    public Film createFilm(Film film) { // Добавление фильма
        try {
            validateFilm(film);
            Film createdFilm;
            createdFilm = filmStorage.createFilm(film);

            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                film.getGenres().stream().distinct().forEach(genre ->
                        genreDao.addGenreToFilm(createdFilm.getId(), genre.getId()));
            }
            log.info("Добавлен новый фильм: {}", film);
            return getFilmById(createdFilm.getId());
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Not Found");
        }
    }

    public Film updateFilm(Film film) { // Обновление фильма
        validateFilm(film);
        getFilmById(film.getId()); // Проверка существования

        filmStorage.updateFilm(film);
        genreDao.removeGenresFromFilm(film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.getGenres().forEach(genre -> genreDao.addGenreToFilm(film.getId(), genre.getId()));
        }

        log.info("Обновлён фильм: {}", film);
        return getFilmById(film.getId());
    }

    public Collection<Film> getAllFilms() { // Получение всех фильмов
        Collection<Film> films = filmStorage.getAllFilms();
        films.forEach(film -> {
            film.setGenres(genreDao.getGenresByFilmId(film.getId()));
            film.setMpa(mpaDao.getMpaRatingById(film.getMpaRatingId()).orElse(null));
        });
        log.info("Получены все фильмы, количество: {}", films.size());
        return films;
    }

    public Film getFilmById(int id) { // Получение фильма по ID
        Film film = filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));

        film.setGenres(genreDao.getGenresByFilmId(id));
        film.setMpa(mpaDao.getMpaRatingById(film.getMpaRatingId()).orElse(null));

        log.info("Получен фильм с id = {}", id);
        return film;
    }

    public void addLike(int filmId, int userId) { // Добавление лайка
        userService.getUserById(userId); // Проверка существования пользователя
        getFilmById(filmId); // Проверка существования фильма

        likeDao.addLike(filmId, userId);
        log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
    }

    public void removeLike(int filmId, int userId) { // Удаление лайка
        userService.getUserById(userId);
        getFilmById(filmId);

        likeDao.removeLike(filmId, userId);
        log.info("Пользователь с id = {} убрал лайк у фильма с id = {}", userId, filmId);
    }

    public Collection<Film> getPopular(int count) { // Получение популярных фильмов
        Collection<Film> popularFilms = filmStorage.getPopular(count).stream()
                .peek(film -> {
                    film.setGenres(genreDao.getGenresByFilmId(film.getId()));
                    film.setMpa(mpaDao.getMpaRatingById(film.getMpaRatingId()).orElse(null));
                })
                .collect(Collectors.toList());

        log.info("Получено {} популярных фильмов", count);
        return popularFilms;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
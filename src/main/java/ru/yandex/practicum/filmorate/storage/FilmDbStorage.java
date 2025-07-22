package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT f.*, m.name AS rating_name, m.description AS rating_description " +
                "FROM films f JOIN mpa_ratings m ON f.rating_id = m.rating_id";
        List<Film> films = jdbcTemplate.query(sql, filmMapper);

        // Загружаем жанры для каждого фильма
        films.forEach(film -> {
            film.setGenres(genreDao.getGenresByFilmId(film.getId()));
            if (film.getMpa() == null) {
                film.setMpa(mpaDao.getMpaRatingById(film.getMpaRatingId()).orElse(null));
            }
        });

        return films;
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        String sql = "SELECT f.*, m.name AS rating_name, m.description AS rating_description " +
                "FROM films f JOIN mpa_ratings m ON f.rating_id = m.rating_id " +
                "WHERE f.film_id = ?";
        return jdbcTemplate.query(sql, filmMapper, id).stream().findFirst();
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        String sql = "SELECT f.*, m.name AS rating_name, COUNT(l.user_id) AS likes_count " +
                "FROM films f LEFT JOIN likes l ON f.film_id = l.film_id " +
                "JOIN mpa_ratings m ON f.rating_id = m.rating_id " +
                "GROUP BY f.film_id ORDER BY likes_count DESC LIMIT ?";
        return jdbcTemplate.query(sql, filmMapper, count);
    }
}
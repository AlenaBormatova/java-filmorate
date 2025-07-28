package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.util.Collection;
import java.util.Optional;

@Component
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;
    private final GenreMapper genreMapper;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate, GenreMapper genreMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreMapper = genreMapper;
    }

    @Override
    public Collection<Genre> getAllGenres() {
        String sql = "SELECT genre_id AS id, name FROM genres ORDER BY genre_id";
        return jdbcTemplate.query(sql, genreMapper);
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        String sql = "SELECT genre_id AS id, name FROM genres WHERE genre_id = ?";
        return jdbcTemplate.query(sql, genreMapper, id).stream().findFirst();
    }

    @Override
    public Collection<Genre> getGenresByFilmId(int filmId) {
        String sql = "SELECT g.genre_id AS id, g.name " +
                "FROM genres g JOIN film_genres fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?";
        return jdbcTemplate.query(sql, genreMapper, filmId);
    }

    @Override
    public void addGenreToFilm(int filmId, int genreId) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    @Override
    public void removeGenresFromFilm(int filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }
}
package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreDbService {
    private final GenreDao genreDao;

    public Collection<Genre> getAllGenres() { // Получение всех жанров
        return genreDao.getAllGenres();
    }

    public Genre getGenreById(int id) { // Получение жанра по ID
        return genreDao.getGenreById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id = " + id + " не найден"));
    }
}
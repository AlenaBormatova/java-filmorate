package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaDbService {
    private final MpaDao mpaDao;

    public Collection<MpaRating> getAllMpaRatings() { // Получение всех рейтингов
        return mpaDao.getAllMpaRatings();
    }

    public MpaRating getMpaRatingById(int id) { // Получение рейтинга по ID
        return mpaDao.getMpaRatingById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id = " + id + " не найден"));
    }
}
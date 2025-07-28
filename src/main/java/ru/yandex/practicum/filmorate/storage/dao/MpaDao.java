package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.Optional;

public interface MpaDao {
    Collection<MpaRating> getAllMpaRatings();

    Optional<MpaRating> getMpaRatingById(int id);
}
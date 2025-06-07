package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {
	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void contextLoads() {
		Assertions.assertTrue(true);
	}

	// Проверка пустого название фильма
	@Test
	void shouldFailWhenNameIsBlank() {
		Film film = Film.builder()
				.name("")  // Невалидное: пустое название
				.description("Описание фильма")
				.releaseDate(LocalDate.of(2000, 1, 1))
				.duration(120)
				.build();

		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Должна быть ошибка валидации для пустого названия.");
	}

	// Проверка слишком ранней даты релиза (граничное условие)
	@Test
	void shouldFailWhenReleaseDateTooEarly() {
		FilmController controller = new FilmController();
		Film film = Film.builder()
				.name("Название фильма")
				.description("Описание фильма")
				.releaseDate(LocalDate.of(1895, 12, 27))  // Невалидная дата (раньше 28.12.1895)
				.duration(120)
				.build();

		assertThrows(ValidationException.class, () -> controller.createFilm(film));
	}

	// Проверка неверного формата email
	@Test
	void shouldFailWhenEmailInvalid() {
		User user = User.builder()
				.email("invalid-email")  // Невалидный email (нет @)
				.login("valid_login")
				.birthday(LocalDate.of(2000, 1, 1))
				.build();

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Должна быть ошибка валидации для неверного email.");
	}

	// Проверка неверного формата login
	@Test
	void shouldFailWhenLoginInvalid() {
		User user = User.builder()
				.email("user@mail.ru")
				.login("invalid login") // Невалидный login (пробел)
				.birthday(LocalDate.of(2000, 1, 1))
				.build();

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Должна быть ошибка валидации для неверного login.");
	}

	// Проверка неверного birthday
	@Test
	void shouldFailWhenBirthdayInvalid() {
		User user = User.builder()
				.email("user@mail.ru")
				.login("valid_login")
				.birthday(LocalDate.of(2040, 1, 1))  // Невалидный birthday (в будущем)
				.build();

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Должна быть ошибка валидации для неверного birthday.");
	}

	// Проверка автоматической подстановки login при отсутствии name
	@Test
	void shouldUseLoginWhenNameIsEmpty() {
		User user = User.builder()
				.name("")
				.login("username")
				.build();

		assertEquals("username", user.getName(), "Должен использовать login, когда name не указан.");
	}

	// Проверка автоматической подстановки login при пустом name
	@Test
	void shouldUseLoginWhenNameIsBlank() {
		User user = User.builder()
				.name("   ")
				.login("username")
				.build();
		assertEquals("username", user.getName(), "Должен использовать login, когда name пустой.");
	}

	// Проверка подстановки name, если оно указано
	@Test
	void shouldUseNameWhenProvided() {
		User user = User.builder()
				.name("Real Name")
				.login("username")
				.build();
		assertEquals("Real Name", user.getName(), "Должен использовать name, если оно указано.");
	}

	// Тест для проверки обработки пустого запроса
	@Test
	void emptyRequestBody() {
		assertThrows(IllegalArgumentException.class, () -> {
			validator.validate(null);  // Имитация пустого запроса
		}, "Должно выбрасываться исключение при пустом теле запроса.");
	}
}
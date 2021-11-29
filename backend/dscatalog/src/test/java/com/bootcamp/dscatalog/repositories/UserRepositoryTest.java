package com.bootcamp.dscatalog.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.bootcamp.dscatalog.entities.User;
import com.bootcamp.dscatalog.tests.Factory;

@DataJpaTest
public class UserRepositoryTest {

	@Autowired
	private UserRepository repository;

	private long existingId;
	private long nonExistingId;
	private long countTotalUsers;

	@BeforeEach
	void setUp() throws Exception {
		// valores de acordo com os dados do h2
		existingId = 1L;
		nonExistingId = 9999L;
		countTotalUsers = 2L;
	}

	@Test
	public void findByIdShouldReturnNonEmptyWhenIdExists() {
		Optional<User> result = repository.findById(existingId);
		assertTrue(result.isPresent());
	}

	@Test
	public void findByIdShouldReturnEmptyWhenIdDoesNotExists() {
		Optional<User> result = repository.findById(nonExistingId);
		assertTrue(result.isEmpty());
	}

	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		User category = Factory.createUser();
		category.setId(null);

		category = repository.save(category);

		assertNotNull(category.getId());
		assertEquals(countTotalUsers + 1, category.getId());
	}

	@Test
	public void deleteShouldDeleteWhenIdExists() {
		repository.deleteById(existingId);
		Optional<User> result = repository.findById(existingId);
		assertFalse(result.isPresent());
	}

	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {
		assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistingId);
		});
	}

	@Test
	public void findByEmailShouldReturnUserWhenEmailExists() {
		String username = "alex@gmail.com";

		User user = repository.findByEmail(username);

		assertNotNull(user);
	}
}

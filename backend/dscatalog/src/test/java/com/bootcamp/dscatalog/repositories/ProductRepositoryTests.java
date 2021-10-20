package com.bootcamp.dscatalog.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.bootcamp.dscatalog.entities.Product;

@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		long existingId = 1;
		
		repository.deleteById(existingId);
		Optional<Product> result = repository.findById(existingId);
		
		assertFalse(result.isPresent());
	}
	
	

}

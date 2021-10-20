package com.bootcamp.dscatalog.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bootcamp.dscatalog.repositories.ProductRepository;

// Não carrega o contexto, mas permite usar os recursos do Spring com Junit(testes de unidade)
@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks // Para o componente que será testado aqui.
	private ProductService service;

	private long existingId;
	private long nonExistingId;

	@Mock
	private ProductRepository repository;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		
		Mockito.doNothing().when(repository).deleteById(existingId);
		// Quando mocamos um objeto, devemos fazer um simulação do comportamento esperado, para determinado método
	
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {

		assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);// Verifica se o método foi chamado.
		// times -> podemos ver se foi chamado determinadas vezes.
	}

}

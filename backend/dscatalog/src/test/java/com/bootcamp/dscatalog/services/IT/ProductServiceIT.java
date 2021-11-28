package com.bootcamp.dscatalog.services.IT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.bootcamp.dscatalog.dto.ProductDTO;
import com.bootcamp.dscatalog.repositories.ProductRepository;
import com.bootcamp.dscatalog.services.ProductService;
import com.bootcamp.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional // Após a execucação de cada teste será dado um rollback para que os outros funcionem com o banco sem alteração.
public class ProductServiceIT {

	@Autowired // Como vamos testar toda a integração, injetamos a classe real.
	private ProductService service;

	@Autowired
	private ProductRepository repository;

	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;

	@BeforeEach
	void setUpd() throws Exception {

		// Como o teste d eintegração vai bater de fato no banco, temos que colocar valores compativeis
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
	}

	@Test
	public void deleteShouldDeleteProductWhenIdExists() {
		Long expectedValue = countTotalProducts - 1;

		service.delete(existingId);

		assertEquals(expectedValue, repository.count());
		// count retorna a quantidade total de registros no banco
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
	}

	@Test
	public void findAllShouldReturnPageWhenPage0IsSize10() {
		PageRequest pageRequest = PageRequest.of(0, 10);// constróia um pagina

		Page<ProductDTO> result = service.findAllPaged(pageRequest);

		assertFalse(result.isEmpty());//Não pode estar vazio
		assertEquals(0, result.getNumber());// Número da página
		assertEquals(10, result.getSize());// Número de elementos na página
		assertEquals(countTotalProducts, result.getTotalElements());// Número total de objetos(todos, independente de estar ou não na página.
	}

	@Test
	public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExists() {
		PageRequest pageRequest = PageRequest.of(50, 10);

		Page<ProductDTO> result = service.findAllPaged(pageRequest);

		assertTrue(result.isEmpty());

	}
	
	@Test
	public void findAllPagedShouldReturnSortedPageWhenSortByName() {
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
		
		Page<ProductDTO> result = service.findAllPaged(pageRequest);

		assertEquals("Macbook Pro", result.getContent().get(0).getName());
		assertEquals("PC Gamer", result.getContent().get(1).getName());
		assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());

	}

}

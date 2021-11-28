package com.bootcamp.dscatalog.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bootcamp.dscatalog.dto.ProductDTO;
import com.bootcamp.dscatalog.entities.Category;
import com.bootcamp.dscatalog.entities.Product;
import com.bootcamp.dscatalog.repositories.CategoryRepository;
import com.bootcamp.dscatalog.repositories.ProductRepository;
import com.bootcamp.dscatalog.services.exceptions.DatabaseException;
import com.bootcamp.dscatalog.services.exceptions.ResourceNotFoundException;
import com.bootcamp.dscatalog.tests.Factory;

// Não carrega o contexto, mas permite usar os recursos do Spring com Junit(testes de unidade)
@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks // Para o componente que será testado aqui.
	private ProductService service;

	private long existingId;
	private long nonExistingId;
	private long dependentId;

	private PageImpl<Product> page;// Tipo concreto do Pageable.
	private Product product;
	private ProductDTO productDTO;
	private Category category;

	@Mock
	private ProductRepository repository;

	@Mock
	private CategoryRepository categoryRepository;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;
		product = Factory.createProduct();
		page = new PageImpl<>(List.of(product));
		productDTO = Factory.createProductDto();
		category = Factory.createCategory();

		Mockito.doNothing().when(repository).deleteById(existingId);
		// Quando mocamos um objeto, devemos fazer um simulação do comportamento
		// esperado, para determinado método

		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		// Métodos void, primeiro temos ação e depois o when.

		// Métodos com retorno, primeiro usamos o when e depois a ação.

		Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
		// ArgumArgumentMatchers - Simulação, n importa muito o tipo, podendo ser
		// "qualquer coisa"
		// Fizemos o cast, por conta da sobrecarga do findAll que espera tipos
		// específicos.

		// Simulando comportamento do save
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

		// Simulando comportamento do findByid com Id EXISTENTE.
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));// Instância optional com
																						// produto.

		// Simulando comportamento do findByid com Id INEXISTENTE.
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

		// Simulando comportamento do getOne com Id EXISTENTE
		Mockito.when(repository.getOne(existingId)).thenReturn(product);

		// Simulando comportamento do getOne com Id EXISTENTE
		Mockito.when(repository.getOne(nonExistingId)).thenThrow(ResourceNotFoundException.class);

		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(ResourceNotFoundException.class);

	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {

		assertDoesNotThrow(() -> {
			service.delete(existingId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);// Verifica se o método foi chamado.
		// times -> podemos ver se foi chamado determinadas vezes.
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}

	@Test
	public void findAllPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.findAllPaged(pageable);

		assertNotNull(result);
		Mockito.verify(repository).findAll(pageable);
	}

	@Test
	public void findByIdShouldReturnDtoWhenIdExists() {
		ProductDTO dto = service.findById(existingId);
		assertNotNull(dto);
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
	}

	@Test
	public void updateShouldReturnDtoWhenIdExists() {
		ProductDTO dto = service.update(existingId, productDTO);

		assertNotNull(dto);
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, productDTO);
		});
	}
	
	@Test
	public void insertShouldReturnCreatedAndId() {
		ProductDTO dto = service.insert(productDTO);
		assertNotNull(dto);
	}

}

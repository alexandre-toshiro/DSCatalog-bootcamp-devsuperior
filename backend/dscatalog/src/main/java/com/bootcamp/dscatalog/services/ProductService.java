package com.bootcamp.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bootcamp.dscatalog.dto.CategoryDTO;
import com.bootcamp.dscatalog.dto.ProductDTO;
import com.bootcamp.dscatalog.entities.Category;
import com.bootcamp.dscatalog.entities.Product;
import com.bootcamp.dscatalog.repositories.CategoryRepository;
import com.bootcamp.dscatalog.repositories.ProductRepository;
import com.bootcamp.dscatalog.services.exceptions.DatabaseException;
import com.bootcamp.dscatalog.services.exceptions.ResourceNotFoundException;

@Service // Registra como componente - Injeção de Dependência.
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional(readOnly = true)//evita o lock no BD, pois n precisamos travar o banco apenas para leitura.
	public Page<ProductDTO> findAllPaged(Pageable pageable) {
		//Devemos retornar um DTO para a camada de controller.
		Page<Product> list = repository.findAll(pageable); // 1)- fazemos a busca de categorias e guardamos numa lista.
		return list.map(x -> new ProductDTO(x)); //2) - Fazemos a conversão para dto e devolvemos apenas ele para o controlador.
		
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);// Retorna um optional, pode ou não ter retornado a entidade
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found.")); // Aqui de fato obetemos a entidade dentro do optional.Se não existir, será instanciada uma exception.
		return new ProductDTO(entity, entity.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}

	
	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		
		try {// Pode ocorrer um erro desse id não existir no banco, então devemos tratar e lançar a nossa exceção que está tratada pelo ControllerAdvice
		Product entity = repository.getOne(id);
		//getOne - Instância um objeto provisório desse objeto sem ir ao banco, necessário para não ir ao banco duas vezes, apenas para 1 update.
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);// agora de fato irá ao banco salvar as alterações.
		return new ProductDTO(entity);
		}
		catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		}
	}

	public void delete(Long id) {
		
		try {
		repository.deleteById(id);
		}
		catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		}
		catch(DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
			// Quando tentar deletar uma categoria que ainda possui relacionamento com um ou mais produtos.
		}
		
	}
	
	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());
		
		entity.getCategories().clear();
		
		for(CategoryDTO catDto : dto.getCategories()) {
			Category category = categoryRepository.getOne(catDto.getId());
			entity.getCategories().add(category);
		}
	}
}






























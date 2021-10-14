package com.bootcamp.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bootcamp.dscatalog.dto.CategoryDTO;
import com.bootcamp.dscatalog.entities.Category;
import com.bootcamp.dscatalog.repositories.CategoryRepository;
import com.bootcamp.dscatalog.services.exceptions.ResourceNotFoundException;

@Service // Registra como componente - Injeção de Dependência.
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)//evita o lock no BD, pois n precisamos travar o banco apenas para leitura.
	public List<CategoryDTO> findAll() {
		//Devemos retornar um DTO para a camada de controller.
		List<Category> list = repository.findAll(); // 1)- fazemos a busca de categorias e guardamos numa lista.
		return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList()); //2) - Fazemos a conversão para dto e devolvemos apenas ele para o controlador.
		
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id);// Retorna um optional, pode ou não ter retornado a entidade
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found.")); // Aqui de fato obetemos a entidade dentro do optional.Se não existir, será instanciada uma exception.
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		
		try {// Pode ocorrer um erro desse id não existir no banco, então devemos tratar e lançar a nossa exceção que está tratada pelo ControllerAdvice
		Category entity = repository.getOne(id);
		//getOne - Instância um objeto provisório desse objeto sem ir ao banco, necessário para não ir ao banco duas vezes, apenas para 1 update.
		entity.setName(dto.getName());
		entity = repository.save(entity);// agora de fato irá ao banco salvar as alterações.
		return new CategoryDTO(entity);
		}
		catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		}
	}

}

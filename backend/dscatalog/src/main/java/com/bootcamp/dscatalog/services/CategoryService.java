package com.bootcamp.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bootcamp.dscatalog.dto.CategoryDTO;
import com.bootcamp.dscatalog.entities.Category;
import com.bootcamp.dscatalog.repositories.CategoryRepository;
import com.bootcamp.dscatalog.services.exceptions.EntityNotFoundException;

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
		Category entity = obj.orElseThrow(() -> new EntityNotFoundException("Entity not found.")); // Aqui de fato obetemos a entidade dentro do optional.Se não existir, será instanciada uma exception.
		return new CategoryDTO(entity);
	}

}

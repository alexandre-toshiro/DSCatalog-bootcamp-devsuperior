package com.bootcamp.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bootcamp.dscatalog.entities.Category;
import com.bootcamp.dscatalog.repositories.CategoryRepository;

@Service // Registra como componente - Injeção de Dependência.
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)//evita o lock no BD, pois n precisamos travar o banco apenas para leitura.
	public List<Category> findAll() {
		return repository.findAll();
	}

}

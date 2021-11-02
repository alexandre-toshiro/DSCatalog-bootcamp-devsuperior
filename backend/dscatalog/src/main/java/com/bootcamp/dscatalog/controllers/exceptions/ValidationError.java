package com.bootcamp.dscatalog.controllers.exceptions;

import java.util.ArrayList;
import java.util.List;

// Possui tudo do StandardError mais uma lista dos FieldError
public class ValidationError extends StandardError {
	
	// Classe criada para capturar as possíveis mensagens de erro que podem vir nas validações com BeanValidation
	// E devolve-las de uma forma legível e amigável

	private static final long serialVersionUID = 1L;
	
	private List<FieldMessage> errors = new ArrayList<>();

	public List<FieldMessage> getErrors() {
		return errors;
	}

	public void addError(String fieldName, String message) {
		errors.add(new FieldMessage(fieldName, message));
	}
	

}

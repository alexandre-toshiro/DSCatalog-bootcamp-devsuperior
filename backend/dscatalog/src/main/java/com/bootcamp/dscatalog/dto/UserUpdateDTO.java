package com.bootcamp.dscatalog.dto;

import com.bootcamp.dscatalog.services.validation.UserUpdateValid;

@UserUpdateValid
public class UserUpdateDTO extends UserDTO {

	private static final long serialVersionUID = 1L;
	/*
	 * Classe criada, apenas para poder implementar a nossa validação customizada.
	 * Pois não podemos colocar essa anotação na UserDTO, já que a UserInsertDTO, herdaria também
	 * o comportamento de validação de uma atualização, o que poderia dar uma conflito.
	 * Então fazemos um DTO específico apenas para isso, dentro de casos assim.
	 * Devemos refatorar o Controller e o Service do método update.
	 * */

}

package com.bootcamp.dscatalog.dto;

import com.bootcamp.dscatalog.entities.User;
import com.bootcamp.dscatalog.services.validation.UserInsertValid;

@UserInsertValid // Processa a nossa validação criada
public class UserInsertDTO extends UserDTO {
	// dto usado somente para inserir um usuário, já que irá receber a senha
	// por se tratar de um dado sensível, para não retornar via api a senha do usuário foi criado um dto especifico para receber senha
	// No método de "insert" dentro do Service de usuário, recebemos este DTO, encriptamos a senha
	// e depois devolvemos para o controlador o UserDto comum, ou seja este dto somente será usado APENAS quando for criado um novo usuário

	private static final long serialVersionUID = 1L;

	private String password;

	public UserInsertDTO() {
		super();
	}
	public UserInsertDTO(User user) {
		super(user);
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}

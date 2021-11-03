package com.bootcamp.dscatalog.dto;

import com.bootcamp.dscatalog.services.validation.UserInsertValid;

@UserInsertValid // Processa a nossa validação criada
public class UserInsertDTO extends UserDTO {

	private static final long serialVersionUID = 1L;

	private String password;

	public UserInsertDTO() {
		super();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}

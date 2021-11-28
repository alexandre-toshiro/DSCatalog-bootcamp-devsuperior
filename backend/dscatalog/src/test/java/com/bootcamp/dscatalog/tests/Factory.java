package com.bootcamp.dscatalog.tests;

import java.time.Instant;

import com.bootcamp.dscatalog.dto.CategoryDTO;
import com.bootcamp.dscatalog.dto.ProductDTO;
import com.bootcamp.dscatalog.dto.UserDTO;
import com.bootcamp.dscatalog.dto.UserInsertDTO;
import com.bootcamp.dscatalog.dto.UserUpdateDTO;
import com.bootcamp.dscatalog.entities.Category;
import com.bootcamp.dscatalog.entities.Product;
import com.bootcamp.dscatalog.entities.Role;
import com.bootcamp.dscatalog.entities.User;

import net.bytebuddy.implementation.bind.annotation.Super;

public class Factory {

	public static Product createProduct() {
		Product product = new Product(1L, "Phone", "Good Phone", 800.0, "http://img.com/img.png",
				Instant.parse("2020-10-20T03:00:00Z"));
		product.getCategories().add(createCategory());
		return product;
	}

	public static ProductDTO createProductDto() {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());
	}

	public static Category createCategory() {
		return new Category(2L, "Electronics");
	}
	
	public static CategoryDTO createCategoryDto() {
		Category category = createCategory();
		return new CategoryDTO(category);
	}
	
	public static User createUser() {
		Role role = new Role(1L, "ROLE_ADMIN");
		User user = new User(1L," Fernando", "Fera", "fera@gmail.com", "123456");
		user.getRoles().add(role);
		return user;
	}
	
	public static UserDTO createUserDto() {
		User user = createUser();
		return new UserDTO(user);
	}
	
	public static UserInsertDTO createUserInsertDto() {
		return new UserInsertDTO(createUser());
	}
	
	public static UserUpdateDTO createUserUpdateDto() {
		return new UserUpdateDTO();
	}

}

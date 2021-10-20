package com.bootcamp.dscatalog.tests;

import java.time.Instant;

import com.bootcamp.dscatalog.dto.ProductDTO;
import com.bootcamp.dscatalog.entities.Category;
import com.bootcamp.dscatalog.entities.Product;

public class Factory {

	public static Product createProduct() {
		Product product = new Product(1L, "Phone", "Good Phone", 800.0, "http://img.com/img.png",
				Instant.parse("2020-10-20T03:00:00Z"));
		product.getCategories().add(new Category(2L, "Electronics"));
		return product;
	}

	public static ProductDTO createProductDto() {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());

	}

}

package com.bootcamp.dscatalog.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import com.bootcamp.dscatalog.dto.ProductDTO;
import com.bootcamp.dscatalog.services.ProductService;
import com.bootcamp.dscatalog.tests.Factory;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {

	@Autowired
	private MockMvc mockMvc;// Abordagem utilizada para chamar endpoints.

	@MockBean
	private ProductService service;

	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	//PageImpl - Página concreta

	@BeforeEach
	void setUp() throws Exception {

		productDTO = Factory.createProductDto();
		page = new PageImpl<>(List.of(productDTO));

		when(service.findAllPaged(any())).thenReturn(page);

	}
	
	@Test
	public void findAllShouldReturnPage() throws Exception {
		//perform - faz uma requisição.
		mockMvc.perform(get("/products")).andExpect(status().isOk());
	}

}

package com.bootcamp.dscatalog.controllers.IT;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.bootcamp.dscatalog.dto.ProductDTO;
import com.bootcamp.dscatalog.tests.Factory;
import com.bootcamp.dscatalog.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	
	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;
	
	//token
	@Autowired
	private TokenUtil tokenUtil;
	
	private String operatorUsername;
	private String operatorPassword;
	private String adminUsername;
	private String adminPassword;

	@BeforeEach
	void setUpd() throws Exception {

		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
		
		//token
		operatorUsername = "alex@gmail.com";
		operatorPassword = "123456";
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
	}

	@Test
	public void findALLShouldReturnSortedPageWhenSortByName() throws Exception {
		ResultActions result = mockMvc
				.perform(get("/products?page=0&size=12&sort=name,asc").accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.totalElements").value(countTotalProducts)); // total de elementos que fica na parte de baixo da resposta
		result.andExpect(jsonPath("$.content").exists()); // a resposta paginada possui o "content"
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro")); // ordem dos produtos dentro do content pelo nome
		result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));

	}

	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
		ProductDTO productDTO = Factory.createProductDto();
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		String expectedName = productDTO.getName();
		String expectedDescription = productDTO.getDescription();
		
		String token = tokenUtil.obtainAccessToken(mockMvc, operatorUsername, operatorPassword);


		ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
				.header("Authorization", "Bearer " + token)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(existingId));
		result.andExpect(jsonPath("$.name").value(expectedName));
		result.andExpect(jsonPath("$.description").value(expectedDescription));

	}

	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		ProductDTO productDTO = Factory.createProductDto();
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		String token = tokenUtil.obtainAccessToken(mockMvc, operatorUsername, operatorPassword);

		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId)
				.header("Authorization", "Bearer " + token)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

}

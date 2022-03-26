package com.bootcamp.dscatalog.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.bootcamp.dscatalog.dto.CategoryDTO;
import com.bootcamp.dscatalog.services.CategoryService;
import com.bootcamp.dscatalog.services.exceptions.DatabaseException;
import com.bootcamp.dscatalog.services.exceptions.ResourceNotFoundException;
import com.bootcamp.dscatalog.tests.Factory;
import com.bootcamp.dscatalog.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CategoryService service;

	@Autowired
	private ObjectMapper objectMapper;

	private CategoryDTO categoryDto;
	private PageImpl<CategoryDTO> page;

	private long existingId;
	private long nonExistingId;
	private long dependentId;

	//token
	@Autowired
	private TokenUtil tokenUtil;

	private String operatorUsername;
	private String operatorPassword;
	private String operatorToken;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 3L;
		dependentId = 2L;

		operatorUsername = "alex@gmail.com";
		operatorPassword = "123456";

		operatorToken = tokenUtil.obtainAccessToken(mockMvc, operatorUsername, operatorPassword);

		categoryDto = Factory.createCategoryDto();
		page = new PageImpl<>(List.of(categoryDto));

		Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(service.findById(existingId)).thenReturn(categoryDto);
		Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		Mockito.when(service.insert(ArgumentMatchers.any())).thenReturn(categoryDto);
		Mockito.when(service.update(ArgumentMatchers.eq(existingId), ArgumentMatchers.any())).thenReturn(categoryDto);
		Mockito.when(service.update(ArgumentMatchers.eq(nonExistingId), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);
		Mockito.doNothing().when(service).delete(existingId);
		Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		Mockito.doThrow(DatabaseException.class).when(service).delete(dependentId);
	}

	@Test
	public void findAllShouldReturnPage() throws Exception {
		ResultActions result = mockMvc
				.perform(get("/categories")
				.header("Authorization", "Bearer " + operatorToken)
				.accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnCategoryDTOWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/categories/{id}", existingId)
				.header("Authorization", "Bearer " + operatorToken)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name" ).exists());

	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/categories/{id}", nonExistingId)
				.header("Authorization", "Bearer " + operatorToken)
				.accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isNotFound());// 404
	}
	
	@Test
	public void insertShouldReturnCreatedAndCategoryDTO() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(categoryDto);
		
		ResultActions result = mockMvc.perform(post("/categories")
				.header("Authorization", "Bearer " + operatorToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
	}
	
	@Test
	public void updateShouldReturnCategoryDtoWhenIdExists() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(categoryDto);
		
		ResultActions result = mockMvc.perform(put("/categories/{id}", existingId)
		.header("Authorization", "Bearer " + operatorToken)
		.content(jsonBody)
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name" ).exists());
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(categoryDto);
		
		ResultActions result = mockMvc.perform(put("/categories/{id}", nonExistingId)
				.header("Authorization", "Bearer " + operatorToken)
				.content(jsonBody) 
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
				
				result.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(delete("/categories/{id}", existingId)
				.header("Authorization", "Bearer " + operatorToken));
		
		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		ResultActions result = mockMvc.perform(delete("/categories/{id}", nonExistingId)
				.header("Authorization", "Bearer " + operatorToken));
		
		result.andExpect(status().isNotFound());

	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdIsRelatedToAnotherObject() throws Exception{
		ResultActions result = mockMvc.perform(delete("/categories/{id}", dependentId)
				.header("Authorization", "Bearer " + operatorToken));
		result.andExpect(res -> assertTrue(res.getResolvedException() instanceof DatabaseException));
		
		result.andExpect(status().isBadRequest());
	}
}

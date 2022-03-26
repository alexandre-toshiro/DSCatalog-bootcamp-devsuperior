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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.bootcamp.dscatalog.dto.UserDTO;
import com.bootcamp.dscatalog.services.UserService;
import com.bootcamp.dscatalog.services.exceptions.DatabaseException;
import com.bootcamp.dscatalog.services.exceptions.ResourceNotFoundException;
import com.bootcamp.dscatalog.tests.Factory;
import com.bootcamp.dscatalog.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService service;

	@Autowired
	private ObjectMapper objectMapper;
	

	private UserDTO userDto;
	private PageImpl<UserDTO> page;

	private long existingId;
	private long nonExistingId;
	private long dependentId;

	//token
	@Autowired
	private TokenUtil tokenUtil;

	private String adminUsername;
	private String adminPassword;
	private String adminToken;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 3L;
		dependentId = 2L;

		adminUsername = "maria@gmail.com";
		adminPassword = "123456";

		adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

		userDto = Factory.createUserDto();
		page = new PageImpl<>(List.of(userDto));

		Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(service.findById(existingId)).thenReturn(userDto);
		Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		Mockito.when(service.insert(ArgumentMatchers.any())).thenReturn(userDto);
		Mockito.when(service.update(ArgumentMatchers.eq(existingId), ArgumentMatchers.any())).thenReturn(userDto);
		Mockito.when(service.update(ArgumentMatchers.eq(nonExistingId), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);
		Mockito.doNothing().when(service).delete(existingId);
		Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		Mockito.doThrow(DatabaseException.class).when(service).delete(dependentId);
	}

	@Test
	public void findAllShouldReturnPage() throws Exception {
		ResultActions result = mockMvc
				.perform(get("/users")
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isOk());
	}
	
	@Test
	//@WithUserDetails(userDetailsServiceBeanName = UserService.class)
	public void findByIdShouldReturnUserDTOWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/users/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name" ).exists());

	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/users/{id}", nonExistingId)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isNotFound());// 404
	}
	
	@Test
	public void insertShouldReturnCreatedAndUserDTO() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(userDto);
		
		ResultActions result = mockMvc.perform(post("/users")
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
	}
	
	@Test
	public void updateShouldReturnUserDtoWhenIdExists() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(userDto);
		
		ResultActions result = mockMvc.perform(put("/users/{id}", existingId)
		.header("Authorization", "Bearer " + adminToken)
		.content(jsonBody)
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name" ).exists());
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(userDto);
		
		ResultActions result = mockMvc.perform(put("/users/{id}", nonExistingId)
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody) 
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
				
				result.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(delete("/users/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken));
		
		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		ResultActions result = mockMvc.perform(delete("/users/{id}", nonExistingId)
				.header("Authorization", "Bearer " + adminToken));
		
		result.andExpect(status().isNotFound());

	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdIsRelatedToAnotherObject() throws Exception{
		ResultActions result = mockMvc.perform(delete("/users/{id}", dependentId)
				.header("Authorization", "Bearer " + adminToken));
		result.andExpect(res -> assertTrue(res.getResolvedException() instanceof DatabaseException));
		
		result.andExpect(status().isBadRequest());
	}
}

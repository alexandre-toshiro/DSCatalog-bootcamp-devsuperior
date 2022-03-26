package com.bootcamp.dscatalog.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.bootcamp.dscatalog.dto.ProductDTO;
import com.bootcamp.dscatalog.services.ProductService;
import com.bootcamp.dscatalog.services.exceptions.DatabaseException;
import com.bootcamp.dscatalog.services.exceptions.ResourceNotFoundException;
import com.bootcamp.dscatalog.tests.Factory;
import com.bootcamp.dscatalog.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTests {

	@Autowired
	private MockMvc mockMvc;// Abordagem utilizada para chamar endpoints.

	@MockBean
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper; // objeto auxíliar
 	// Não ferindo o principio de teste de unidade.

	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	// PageImpl - Página concreta

	private long existingId;
	private long nonExistingId;
	private long dependentId;
	
		//token
		@Autowired
		private TokenUtil tokenUtil;
		
		private String operatorUsername;
		private String operatorPassword;
		private String adminUsername;
		private String adminPassword;
		private String operatorToken;
		private String adminToken ;

	@BeforeEach
	void setUp() throws Exception {
		
		 existingId = 1L;
		 nonExistingId = 2L;
		 dependentId = 3L;

		productDTO = Factory.createProductDto();
		page = new PageImpl<>(List.of(productDTO));

		when(service.findAllPaged(any())).thenReturn(page);

		when(service.findById(existingId)).thenReturn(productDTO);
		when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		
		when(service.update(eq(existingId), any())).thenReturn(productDTO);
		when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
		
		doNothing().when(service).delete(existingId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		doThrow(DatabaseException.class).when(service).delete(dependentId);
		
		when(service.insert(any())).thenReturn(productDTO);
		
		//token
	   operatorUsername = "alex@gmail.com";
	   operatorPassword = "123456";
	   adminUsername = "maria@gmail.com";
	   adminPassword = "123456";
	   
		 operatorToken = tokenUtil.obtainAccessToken(mockMvc, operatorUsername, operatorPassword);
		 adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
	}

	@Test
	public void findAllShouldReturnPage() throws Exception {
		// perform - faz uma requisição.
		ResultActions result = mockMvc.perform(get("/products")
				.header("Authorization", "Bearer " + operatorToken)
				.accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isOk());
	}

	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());// verificar se existe aquele atributo no objeto da resposta.
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		// $ - Acessa o objeto da resposta(análisa o json
		// Podemos análisar apenas alguns atributos ou todos.
	}

	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingId)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isNotFound());// 404
		// Como na camada de controller a ResourceNotFound foi tratada pelo controllerAdvice
		// Não irá retornar a exceção, sim o status 404 - notFound
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
		// No update será enviado também um corpo na requisição, portanto devemos simula-lo aqui.
		// Utilizaremos a dependência auxíliar "ObjectMapper"
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
				.header("Authorization", "Bearer " + adminToken)
				.content(jsonBody) // conteúdo que irá no corpo da requisição
				.contentType(MediaType.APPLICATION_JSON)// tipo do conteúdo
				.accept(MediaType.APPLICATION_JSON));
				
				result.andExpect(status().isOk());
				result.andExpect(jsonPath("$.id").exists());
 				result.andExpect(jsonPath("$.description").exists());
		
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId)
				.header("Authorization", "Bearer " + operatorToken)
				.content(jsonBody) // conteúdo que irá no corpo da requisição
				.contentType(MediaType.APPLICATION_JSON)// tipo do conteúdo
				.accept(MediaType.APPLICATION_JSON));
				
				result.andExpect(status().isNotFound());
	}
	
	@Test
	public void insertShouldReturnCreatedAndProductDTO() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc.perform(post("/products")
				.header("Authorization", "Bearer " + operatorToken)
				.content(jsonBody) 
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());

	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(delete("/products/{id}", existingId)
				.header("Authorization", "Bearer " + operatorToken));
		
		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		ResultActions result = mockMvc.perform(delete("/products/{id}", nonExistingId)
				.header("Authorization", "Bearer " + operatorToken));
		
		result.andExpect(status().isNotFound());

	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdIsRelatedToAnotherObject() throws Exception {
		ResultActions result = mockMvc.perform(delete("/products/{id}", dependentId)
				.header("Authorization", "Bearer " + operatorToken));
		result.andExpect(res -> assertTrue(res.getResolvedException() instanceof DatabaseException ));
		//1- Fazendo uma expressão lambda para ver a exceção retornada quando for um id dependente.
		// 3 - No caso código acima serve para ver especificamente o tipo da exceção que está retornando
		// mesmo quando tratada pelo ControllerAdvice
		
		result.andExpect(status().isBadRequest());
		// 2 - Exceção está sendo tratada no ControllerAdvice e está retornando um status e não a exceção em si.
		
		// Pode deixar as duas verificações descomentadas, uma não anula a outra.
	
	}

}

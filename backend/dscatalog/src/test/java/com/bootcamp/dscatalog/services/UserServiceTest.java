package com.bootcamp.dscatalog.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bootcamp.dscatalog.dto.UserDTO;
import com.bootcamp.dscatalog.dto.UserInsertDTO;
import com.bootcamp.dscatalog.dto.UserUpdateDTO;
import com.bootcamp.dscatalog.entities.User;
import com.bootcamp.dscatalog.repositories.RoleRepository;
import com.bootcamp.dscatalog.repositories.UserRepository;
import com.bootcamp.dscatalog.services.exceptions.DatabaseException;
import com.bootcamp.dscatalog.services.exceptions.ResourceNotFoundException;
import com.bootcamp.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;

	@Mock
	private RoleRepository roleRepository;

	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private String username;
	private String usernameNull;

	private PageImpl<User> page;
	private User user;
	private UserDTO dto;
	private UserInsertDTO insertDto;
	private UserUpdateDTO updateDto;

	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 5L;
		user = Factory.createUser();
		page = new PageImpl<>(List.of(user));
		dto = Factory.createUserDto();
		insertDto = Factory.createUserInsertDto();
		updateDto = Factory.createUserUpdateDto();
		username = "alex@gamil.com";
		usernameNull = null;

		Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(user));
		Mockito.when(repository.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(user);
		Mockito.when(repository.getOne(existingId)).thenReturn(user);
		Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		Mockito.when(repository.findByEmail(username)).thenReturn(user);
		//Mockito.when(repository.findByEmail(usernameNull)).thenThrow(UsernameNotFoundException.class);
	}

	@Test
	public void findAllPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<UserDTO> resultPage = service.findAllPaged(pageable);

		assertNotNull(resultPage);
		verify(repository).findAll(pageable);
	}

	@Test
	public void findByIdShouldReturnDtoWhenIdExists() {
		UserDTO dto = service.findById(existingId);
		assertNotNull(dto);
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
	}

	@Test
	public void insertShouldReturnDtoWhenUserIsCreated() {
		UserDTO dto = service.insert(insertDto);
		assertNotNull(dto);
	}

	@Test
	public void updateShouldReturnDtoWhenIdIsExists() {
		UserDTO dto = service.update(existingId, updateDto);
		assertNotNull(dto);
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, updateDto);
		});
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		assertDoesNotThrow(() -> service.delete(existingId));

		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);

	}

	@Test
	public void loadUserByUsernameShouldReturnUser() {
			User user = (User) service.loadUserByUsername(username);
			assertNotNull(user);
			
			Mockito.verify(repository, Mockito.times(1)).findByEmail(username);
	}
	
	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserIsNull() {
		assertThrows(UsernameNotFoundException.class, () -> {
			service.loadUserByUsername(usernameNull);
		});	
		Mockito.verify(repository, Mockito.times(1)).findByEmail(usernameNull);
	}
	
	

}

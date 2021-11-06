package com.bootcamp.dscatalog.components;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.bootcamp.dscatalog.entities.User;
import com.bootcamp.dscatalog.repositories.UserRepository;

@Component
public class JwtTokenEnhancer implements TokenEnhancer {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		User user = userRepository.findByEmail(authentication.getName());
		
		Map<String, Object> map = new HashMap<>();
		map.put("userFirstName", user.getFirstName());
		map.put("userId", user.getId());
		
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
		// downcast, pois dentro do OAuth2AccessToken, não possui os métodos necessário para acrescentar info no token
		// a classe DefaultOAuth2AccessToken é tipo mais específico da OAuth2AccessToken
		token.setAdditionalInformation(map);
		
		return accessToken;
	
		
	}

}

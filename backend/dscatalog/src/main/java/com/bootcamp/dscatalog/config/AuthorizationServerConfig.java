package com.bootcamp.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer // indica que esta classe representa o AuthorizationServer do Oauth
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	
	@Value("${security.oauth2.client.client-id}")
	private String clientId;
	
	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;
	
	@Value("${jwt.duration}")
	private Integer jwtDuration;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtAccessTokenConverter accessTokenConverter;
	
	@Autowired
	private JwtTokenStore tokenStore;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
		// Esse método configura a autorização do AuthorizationServer
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// Esse método define como será a autenticação e quais vão ser os dados do cliente.
		clients.inMemory() // processo será feito em memória
		.withClient(clientId) // Definimos o clientId/nome da aplicação - a aplicação web deverá informar este nome
		.secret(passwordEncoder.encode(clientSecret)) // Senha da aplicação
		.scopes("read", "write") // acesso de leitura e escrita
		.authorizedGrantTypes("password")// padrão do oauth
		.accessTokenValiditySeconds(jwtDuration);// tem em segundos da duração do token
	
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		// quem irá autorizar e qual vai ser o formato do token
		endpoints.authenticationManager(authenticationManager)
		.tokenStore(tokenStore)
		.accessTokenConverter(accessTokenConverter);
	}

	
}

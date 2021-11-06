package com.bootcamp.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
	@Autowired
	private JwtTokenStore tokenStore;
	
	private static final String[] PUBLIC = {"/oauth/token"};
	private static final String[] OPERATOR_OR_ADMIN = {"/products/**", "/categories/**"};
	
	private static final String[] ADMIN = {"/users/**"};

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		//configuramos o tokenstore
		resources.tokenStore(tokenStore);
		//com isso o resourceserver consegue receber e avaliar se o token mandado é válido.
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		//Aqui configuramos as rotas.
		http.authorizeRequests()
		.antMatchers(PUBLIC).permitAll()
		.antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll()// libera AS ROTAS na função GET(todo mundo pode ver os produtos)
		.antMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN")
		.antMatchers(ADMIN).hasRole("ADMIN")
		.anyRequest().authenticated();		
	}
}

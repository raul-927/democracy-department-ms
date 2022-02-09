package com.democracy.department.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

@SuppressWarnings("deprecation")
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
    @Autowired
    private DataSource dataSource;
    
   @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenStore(tokenStore()).authenticationEntryPoint(authenticationEntryPoint);
        System.out.println("AAAAA: "+resources.getTokenStore().toString());
    }
    
    @Override
    public void configure(HttpSecurity http) throws Exception {

        // add http.cors()
    	//http.cors().and().csrf().disable();
        http.cors().and().csrf().disable().authorizeRequests()
                //.antMatchers("/login/**").permitAll()
                .anyRequest().authenticated(); // Authenticate users with HTTP basic authentication

        // REST is stateless
        //http.sessionManagement()
          //     .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
    
    @Bean
    public TokenStore tokenStore() {
    	JdbcTokenStore jdb =  new JdbcTokenStore(dataSource);
    	System.out.println("jdb STORE: "+jdb.toString());
        return jdb;
    }
    
   /*
  @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        List<String> listConfig = new ArrayList<>();
        List<String>listMethod = new ArrayList<>();
        List<String>listHeaders = new ArrayList<>();
        
        listConfig.add("*");
        
        listMethod.add("GET");
        listMethod.add("POST");
        listMethod.add("PUT");
        listMethod.add("DELETE");
        
        listHeaders.add("Authorization");
        listHeaders.add("Cache-Control");
        listHeaders.add("Content-Type");
        
        configuration.setAllowedOrigins(listConfig); // www - obligatory
//        configuration.setAllowedOrigins(ImmutableList.of("*"));  //set access from all domains
        configuration.setAllowedMethods(listMethod);
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(listHeaders);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
   */
}

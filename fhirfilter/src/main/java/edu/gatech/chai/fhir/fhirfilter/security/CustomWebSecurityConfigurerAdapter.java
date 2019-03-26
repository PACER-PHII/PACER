package edu.gatech.chai.fhir.fhirfilter.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
	@Autowired
	private MyBasicAuthenticationEntryPoint authenticationEntryPoint;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().authorizeRequests()
			.antMatchers("/manage/**").authenticated()
			.anyRequest().permitAll().and()
			.httpBasic().authenticationEntryPoint(authenticationEntryPoint);
		
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class);
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		String user = System.getenv("BASIC_AUTH_USER");
		String pass = System.getenv("BASIC_AUTH_PASSWORD");

//		auth.inMemoryAuthentication().withUser(user).password(passwordEncoder().encode(pass)).roles("USER", "ADMIN");
		auth.inMemoryAuthentication().withUser(user).password(pass).roles("USER", "ADMIN");

		//		auth.inMemoryAuthentication().withUser(user).password(pass).roles("USER");

//		System.out.println("User:"+user+", Password:"+pass);
//		auth.inMemoryAuthentication().withUser(user).password(pass).roles("USER")
//        .and().withUser(user).password(pass).roles("USER", "ADMIN");
//		auth.inMemoryAuthentication().withUser(user).password(passwordEncoder().encode(pass)).authorities("ROLE_ADMIN");
	}

//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.cors().and().csrf().disable().authorizeRequests().antMatchers("/apply", "/apply/**", "/swagger-ui.html").permitAll().anyRequest().authenticated().and()
//				.httpBasic().authenticationEntryPoint(authenticationEntryPoint);
//
//		http.sessionManagement()
//	    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//		
////		http.authorizeRequests().antMatchers("/manage", "/manage/**").hasAuthority("ADMIN");
//
//		http.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class);
//	}

//	@Override
//	public void configure(WebSecurity webSecurity) throws Exception {
//		webSecurity.ignoring().antMatchers(HttpMethod.POST, "/manage", "/manage/**")
//				.antMatchers(HttpMethod.PUT, "/manage", "/manage/**")
//				.antMatchers(HttpMethod.DELETE, "/manage", "/manage/**");
//	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

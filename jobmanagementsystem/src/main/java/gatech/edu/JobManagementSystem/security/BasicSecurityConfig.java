package gatech.edu.JobManagementSystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class BasicSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private BasicSecurityProperties basicSecurityProperties;
	
	protected void configure(HttpSecurity http) throws Exception {
	    http
	    .csrf().disable()
	    .authorizeRequests().anyRequest().authenticated()
	    .and().httpBasic();
//	    http.authorizeRequests()
//	      .antMatchers("/**")
//	      .permitAll();
	}
	
	 @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.inMemoryAuthentication()
            .withUser(basicSecurityProperties.getUsername())
            .password("{noop}"+basicSecurityProperties.getPassword())//Assume plaintext, prefix {noop} on credentials
            .roles("USER");
    }

	public BasicSecurityProperties getBasicSecurityProperties() {
		return basicSecurityProperties;
	}

	public void setBasicSecurityProperties(BasicSecurityProperties basicSecurityProperties) {
		this.basicSecurityProperties = basicSecurityProperties;
	}
}
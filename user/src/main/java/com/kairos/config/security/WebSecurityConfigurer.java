package com.kairos.config.security;

import com.kairos.service.auth.UserOauth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.kairos.constants.AppConstants.*;


@Configuration
@EnableWebSecurity
@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserOauth2Service userDetailsService;
    
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        auth.authenticationProvider(authenticationProvider());
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /*
	 * @see org.springframework.security.config.annotation.web.configuration.
	 * WebSecurityConfigurerAdapter#configure(org.springframework.security.
	 * config.annotation.web.builders.HttpSecurity) This method is where the
	 * actual URL-based security is set up.
	 */

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
        web.ignoring().antMatchers("/webjars/**");
        web.ignoring().antMatchers("/swagger-resources/**/**");
        web.ignoring().antMatchers("/swagger-ui.html");
        web.ignoring().antMatchers("/v2/api-docs");
        web.ignoring().antMatchers("/api/v1/organization/{organizationId}/ids");
        web.ignoring().antMatchers("/api/v1/organization/{organizationId}/unit/{unitId}/WithoutAuth");
        web.ignoring().antMatchers("/api/v1/time_care/**");
        web.ignoring().antMatchers(API_KMD_CARE_CITIZEN_GRANTS);
        web.ignoring().antMatchers(API_KMD_CARE_CITIZEN);
        web.ignoring().antMatchers(API_KMD_CARE_CITIZEN_RELATIVE_DATA);
        web.ignoring().antMatchers(API_KMD_CARE_STAFF_SHIFTS);
        web.ignoring().antMatchers(API_TIME_CARE_SHIFTS);
        web.ignoring().antMatchers(API_TIME_SLOTS_NAME);
        web.ignoring().antMatchers(API_KMD_CARE_TIME_SLOTS);
        web.ignoring().antMatchers("/api/v1/organization/{organizationId}/unit/{unitId}/client/client_ids_by_unitIds");


        web.ignoring().antMatchers("/api/v1/login");

    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
    	 http
                  .csrf().disable()
                  .anonymous().disable()
                  .sessionManagement()
                  .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                   .and()
                  .authorizeRequests()
                  .antMatchers("/oauth/*").permitAll()
                   .antMatchers(HttpMethod.OPTIONS).permitAll()
                   .anyRequest().authenticated()
                   .and()
                 .formLogin();
    }
}

package com.kairos.config.security;

import com.kairos.service.auth.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.inject.Inject;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.AppConstants.*;

/**
  Applies Spring security
 1.White list some URLs
 2.Redirection to login page for unauthorized User.
 3.Password encryption
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    @Inject
    ApplicationContext applicationContext;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Exclude API_V1 from Role based security.

        http.authorizeRequests()
                .antMatchers(
                        "/api/v1/**",
                        "/api/v1/login",
                        API_LOGOUT_URL,
                        "/swagger-ui.html",
                        "/swagger-resources/**/**",
                        "/springfox**/**",
                        "/webjars/**",
                        "/v2/api-docs",
                        API_V1+"/kairos/ws/**",
                        API_V1+"/messaging/manage/pushToQueue",
                        API_TIME_CARE_SHIFTS,
                        API_TIME_CARE_ACTIVITIES,
                        API_KMD_CARE_CITIZEN,
                        API_KMD_CARE_CITIZEN_GRANTS,
                        API_KMD_CARE_CITIZEN_RELATIVE_DATA,
                        API_CREDENTIAL_UPDATE_URL,
                        "/monitoring"
                        ,"/health"


                ).permitAll();

        http.authorizeRequests()
                .anyRequest().authenticated();

        // for Login
        http.formLogin().failureUrl("/login")
                .permitAll()
                .and()
                .logout().invalidateHttpSession(true).clearAuthentication(true)
                .permitAll();
                 // Disable  CSRF (Cross Site Request Forgery) protection
                 http.csrf().disable();

           //add filters
        http.addFilterBefore(userAuthFilter(),UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(userTokenFilter(),UsernamePasswordAuthenticationFilter.class);
//        http.addFilterAt(userTokenFilter(),UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(final WebSecurity web) throws Exception
    {
        web.ignoring().antMatchers("/swagger-ui.html");
    }

    /**
     *
     * @param auth
     * @throws Exception
     */
    @Inject
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.authenticationProvider(authenticationProvider());

    }

    @Bean
    UserAuthFilter userAuthFilter(){
        UserService userService = (UserService) applicationContext.getBean("userService");
        return new UserAuthFilter("/api/v1/login/auth",userService);
    }

    @Bean UserTokenFilter userTokenFilter(){
        UserService userService = (UserService) applicationContext.getBean("userService");
        return new UserTokenFilter(userService);
    }

    
    /**
     *
     * @return AuthenticationProvider
     */
    @Bean
    AuthenticationProvider authenticationProvider() {

        return new AuthenticationProvider();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

package com.financialapp.financialapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.financialapp.financialapp.service.UserDetailsServiceImpl;

import jakarta.servlet.http.HttpServletResponse;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration{



    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public PasswordEncoder PasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfiguration(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
            http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/plaid/webhook"))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/","/api/whoami","/api/csrf-token","/api/plaid/webhook","/api/login","/api/logout","/api/signup").permitAll()
                .requestMatchers("/**","/api/**","/api/plaid/**").hasAnyRole("USER")
                .anyRequest().authenticated()
                
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> 
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                )
            )
            .userDetailsService(userDetailsService)
            .formLogin(form -> form
                .loginProcessingUrl("/api/login")
                .permitAll()
                .successHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
                .failureHandler((req, res, ex) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
		    )
            .logout(logout -> logout
                .logoutUrl("/api/logout")
                .invalidateHttpSession(true)
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                })
                .deleteCookies("JSESSIONID","remember-me-cookie")
                .permitAll()

            )
            .rememberMe(rememberMe -> rememberMe.rememberMeServices(rememberMeServices(userDetailsService)));


        return http.build();

    }
    @Bean
    public TokenBasedRememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
        TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices(
            "mySuperSecretKey",
            userDetailsService
        );
        rememberMeServices.setAlwaysRemember(false);
        rememberMeServices.setCookieName("remember-me-cookie");
        rememberMeServices.setTokenValiditySeconds(60 * 60 * 24 * 14);
        return rememberMeServices;
    }

    @Bean
    public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
        return new RememberMeAuthenticationProvider("mySuperSecretKey");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://unifyfinance.ca")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }
    /**@Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
            return (web) -> web.debug(true);
        }**///for debugging spring security see also app properties for line starting with logging to enable.

    
}
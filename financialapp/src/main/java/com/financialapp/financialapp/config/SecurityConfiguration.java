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
                .requestMatchers("/","/api/getUserRole","/api/csrf-token","/static/**","/api/plaid/webhook","/api/login").permitAll()
                .requestMatchers("/**").hasAnyRole("USER")
                .anyRequest().authenticated()
                
            )
            .userDetailsService(userDetailsService)
            .formLogin(form -> form
                .loginProcessingUrl("/api/login")
                .permitAll()
                .successHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
                .failureHandler((req, res, ex) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                .failureUrl("http://localhost:5173/signin?error=true")
		    )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID","remember-me-cookie")
                .permitAll()

            )
            .rememberMe(rememberMe -> rememberMe.rememberMeServices(rememberMeServices(userDetailsService)));


        return http.build();

    }
    @Bean
    public TokenBasedRememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
        TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices(
            "mySuperSecretKey", // 🔑 Replace this with a secure random string in prod
            userDetailsService
        );
        rememberMeServices.setAlwaysRemember(false); // only remembers if "remember-me" is checked
        rememberMeServices.setCookieName("remember-me-cookie");
        rememberMeServices.setTokenValiditySeconds(60 * 60 * 24 * 14); // 14 days
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
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }

}
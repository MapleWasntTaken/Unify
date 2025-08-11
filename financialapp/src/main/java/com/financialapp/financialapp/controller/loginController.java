package com.financialapp.financialapp.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.financialapp.financialapp.model.ApplicationUser;
import com.financialapp.financialapp.repository.UserRepository;
import com.financialapp.financialapp.service.CurrentUserService;

@Controller
public class loginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrentUserService currentUserService;
    
    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/api/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest data) {
        String email = data.email;
        String password = data.password;

        if(userRepository.findByEmail(email).isPresent()){return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");}
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password does not meet requirements");
        }
        else{
            ApplicationUser u = new ApplicationUser();
            u.setEmail(email);
            u.setRole("ROLE_USER");
            u.setPassword(encoder.encode(password));
            userRepository.save(u);
            return ResponseEntity.ok("Signup successful");

        }
    }
    @GetMapping("/api/whoami")
    public ResponseEntity<String> whoami() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return ResponseEntity.ok("Logged in");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        
    }


    public static class SignupRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }


}

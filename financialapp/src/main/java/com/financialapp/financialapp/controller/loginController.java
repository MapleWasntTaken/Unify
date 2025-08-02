package com.financialapp.financialapp.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.financialapp.financialapp.model.ApplicationUser;
import com.financialapp.financialapp.repository.UserRepository;

@Controller
public class loginController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/api/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest data) {
        String email = data.email;
        String password = data.password;

        if(userRepository.findByEmail(email).isPresent()){return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");}
        //add password logic here
        else{
            ApplicationUser u = new ApplicationUser();
            u.setEmail(email);
            u.setRole("ROLE_USER");
            u.setPassword(encoder.encode(password));
            userRepository.save(u);
            return ResponseEntity.ok("Signup successful");

        }
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

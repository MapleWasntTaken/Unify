package com.financialapp.financialapp.model;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class ApplicationUser implements UserDetails{
    
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="user_id")
    private Integer userId;

    @Column(unique=true)
    private String email;

    @JsonIgnore
    private String password;

    @Column(name = "user_role")
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<PlaidItem> plaidItems = new ArrayList<>();


    



    public void setRole(String foo){
        if((foo.equals("ROLE_USER"))){
            this.role = foo;
        }
    }
    public String getRole(){
        return this.role;
    }
    public Integer getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void addPlaidItem(PlaidItem plaidItem){
        this.plaidItems.add(plaidItem);
        plaidItem.setUser(this);
    }

    public void removePlaidItem(PlaidItem plaidItem){
        this.plaidItems.remove(plaidItem);
        plaidItem.setUser(null);
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getUsername(){
        return this.email;
    }
    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<PlaidItem> getPlaidItems() {
        return plaidItems;
    }





}

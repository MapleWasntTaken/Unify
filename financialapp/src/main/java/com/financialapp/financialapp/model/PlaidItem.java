package com.financialapp.financialapp.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;



@Entity
public class PlaidItem {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private ApplicationUser user;


    private String plaidItemId;
    private String accessToken;
    private String institutionId;
    private String institutionName;
    private boolean update;
    private boolean filled;
    
    @OneToMany(mappedBy = "plaidItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<AccountItem> accountItems = new ArrayList<>();


    public void setUpdate(boolean val){
        this.update = val;
    }
    public boolean getUpdate(){
        return this.update;
    }

    public List<AccountItem> getAccountItems() {
        return accountItems;
    }

    public String getPlaidItemId() {
        return plaidItemId;
    }

    public void setPlaidItemId(String plaidItemId) {
        this.plaidItemId = plaidItemId;
    }

    @Override
    public String toString() {
        return "PlaidItem [id=" + id + ", user=" + user + ", accessToken=" + accessToken + ", institutionId="
                + institutionId + ", institutionName=" + institutionName;
    }

    public void addAccountItem(AccountItem ai){
        if(!accountItems.contains(ai)){accountItems.add(ai);}
    }
    public void removeAccountItem(AccountItem ai){
        accountItems.remove(ai);
    }


    


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }
    public boolean getFilled() {
        return filled;
    }
    public void setFilled(boolean filled) {
        this.filled = filled;
    }


}

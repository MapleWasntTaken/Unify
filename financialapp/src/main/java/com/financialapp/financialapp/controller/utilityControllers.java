package com.financialapp.financialapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financialapp.financialapp.dto.frontEndAccountItem;
import com.financialapp.financialapp.dto.frontEndTransactionItem;
import com.financialapp.financialapp.model.AccountItem;
import com.financialapp.financialapp.model.ApplicationUser;
import com.financialapp.financialapp.model.PlaidItem;
import com.financialapp.financialapp.model.TransactionItem;
import com.financialapp.financialapp.repository.UserRepository;
import com.financialapp.financialapp.service.CurrentUserService;
import com.financialapp.financialapp.service.PlaidService;

import jakarta.servlet.http.HttpServletRequest;
@RequestMapping("/api")
@RestController
public class utilityControllers {

    @GetMapping("/csrf-token")
    public Map<String, String> getCsrfToken(HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");

        return Map.of(
            "token", token.getToken(),
            "headerName", token.getHeaderName()
        );
    }



    
    private final CurrentUserService currentUserService;
    private final PlaidService plaidService;
    private final UserRepository userRepository;

    public utilityControllers(
        CurrentUserService currentUserService,
        PlaidService plaidService,
        UserRepository userRepository
    ) {
        this.currentUserService = currentUserService;
        this.plaidService = plaidService;
        this.userRepository = userRepository;
    }


    /**
     * 
     * @param authentication User current data
     * @return string of user role
     */
    @GetMapping("/getUserRole")
    public ResponseEntity<String> getCurrentUserRoles(Authentication authentication) {
        if(authentication==null){
            return ResponseEntity.ok("Not Signed In");
        }
        String x =  authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)  
                .collect(Collectors.toList()).get(0);
        return ResponseEntity.ok(x);
    }

    @GetMapping("/getUserData")
    public ResponseEntity<Map<String,Object>> getUserData(){
        
        ApplicationUser wrongTypeUser = currentUserService.getCurrentUser();
        ApplicationUser currentUser = userRepository.getUserWithPlaidItems(wrongTypeUser.getUserId().longValue());
        Map<String,Object> data = new HashMap<>();
        List<frontEndAccountItem> accounts = new ArrayList<frontEndAccountItem>();
        List<frontEndTransactionItem> transactions = new ArrayList();
        List<Map<String,String>> accountStatus = new ArrayList();
        for(PlaidItem pl : currentUser.getPlaidItems()){
            if(pl.getFilled()==false){
                plaidService.refreshPlaidItem(pl);
            }
            Map<String,String> x = new HashMap<>();
            x.put("Filled",Boolean.toString(pl.getFilled()));
            x.put("Update",Boolean.toString(pl.getUpdate()));
            for(AccountItem ai : pl.getAccountItems()){
                frontEndAccountItem feai = new frontEndAccountItem(ai);
                accounts.add(feai);
                for(TransactionItem ti: ai.getTransactions()){
                    frontEndTransactionItem feti = new frontEndTransactionItem(ti);
                    transactions.add(feti);
                }
                x.put("AccountId",ai.getAccountId());
            }
            accountStatus.add(x);
            
        }
        data.put("Accounts",accounts);
        data.put("Transactions",transactions);
        data.put("Statuses",accountStatus);
        
        return ResponseEntity.ok(data);

    }


}

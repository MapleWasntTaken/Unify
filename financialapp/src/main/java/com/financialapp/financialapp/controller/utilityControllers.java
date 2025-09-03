package com.financialapp.financialapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.financialapp.financialapp.config.Env;
import com.financialapp.financialapp.dto.frontEndAccountItem;
import com.financialapp.financialapp.dto.frontEndTransactionItem;
import com.financialapp.financialapp.model.AccountItem;
import com.financialapp.financialapp.model.ApplicationUser;
import com.financialapp.financialapp.model.PlaidItem;
import com.financialapp.financialapp.model.TransactionItem;
import com.financialapp.financialapp.repository.PlaidItemRepository;
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



    
    @Autowired
    private PlaidItemRepository plaidItemRepository;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private PlaidService plaidService;


    public RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private UserRepository userRepository;
 
    @PostMapping("/test")
    public ResponseEntity<String> triggerSandboxWebhook() {

        try {
            // get an existing PlaidItem (you can adjust the lookup as needed)
            PlaidItem dummy = plaidItemRepository.findAll().get(1);

            String accessToken = dummy.getAccessToken();

            // build request payload
            Map<String, String> requestPayload = Map.of(
                "client_id", Env.PLAID_CLIENT_ID,
                "secret", Env.PLAID_SECRET,
                "access_token", accessToken,
                "webhook_type","TRANSACTIONS",
                "webhook_code", "DEFAULT_UPDATE"  // you can change this to e.g., HISTORICAL_UPDATE
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestPayload, headers);

            // make HTTP call to Plaid sandbox
            String sandboxUrl = "https://sandbox.plaid.com/sandbox/item/fire_webhook";
            ResponseEntity<String> plaidResponse = restTemplate.postForEntity(
                    sandboxUrl, requestEntity, String.class);
            return ResponseEntity.ok("Webhook triggered. Plaid responded: " + plaidResponse.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to trigger webhook: " + e.getMessage());
        }
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
                try {
                    plaidService.refreshPlaidItem(pl);
                } catch (Exception e) {
                }
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
        
        System.out.println(data);
        return ResponseEntity.ok(data);

    }


}

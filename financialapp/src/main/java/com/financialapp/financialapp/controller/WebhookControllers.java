package com.financialapp.financialapp.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financialapp.financialapp.model.PlaidItem;
import com.financialapp.financialapp.repository.PlaidItemRepository;
import com.financialapp.financialapp.service.CurrentUserService;
import com.financialapp.financialapp.service.PlaidService;

@RestController
@RequestMapping("/api/plaid")
public class WebhookControllers {
    

    
    @Autowired
    private PlaidService plaidService;

    @Autowired
    private PlaidItemRepository plaidItemRepository;

    @Autowired
    private CurrentUserService currentUserService;


    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        String webhookType = (String) payload.get("webhook_type");
        String webhookCode = (String) payload.get("webhook_code");
        String itemId = (String) payload.get("item_id");
        
        System.out.println(webhookType);


        //On new transaction webhook ping, refreshPlaidItem is called which updates all accounts and transactions for the plaid item corresponding to webhook ping
        if ("TRANSACTIONS".equalsIgnoreCase(webhookType) &&
            "DEFAULT_UPDATE".equalsIgnoreCase(webhookCode)) {
            Optional<PlaidItem> x = plaidItemRepository.findByPlaidItemId(itemId);
            if(x.isPresent()){plaidService.refreshPlaidItem(x.get());}
            
        }

        return ResponseEntity.ok("Received");
    }
}

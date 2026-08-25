package com.financialapp.financialapp.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.financialapp.financialapp.api.PlaidApiException;
import com.financialapp.financialapp.model.ApplicationUser;
import com.financialapp.financialapp.model.PlaidItem;
import com.financialapp.financialapp.repository.UserRepository;
import com.financialapp.financialapp.service.CurrentUserService;
import com.financialapp.financialapp.service.PlaidService;

class UtilityControllersTest {
    @Test
    void getUserDataDoesNotHidePlaidRefreshFailures() {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        PlaidService plaidService = mock(PlaidService.class);
        UserRepository userRepository = mock(UserRepository.class);
        utilityControllers controller = new utilityControllers(
            currentUserService, plaidService, userRepository
        );

        ApplicationUser user = new ApplicationUser();
        user.setUserId(42);
        PlaidItem plaidItem = new PlaidItem();
        plaidItem.setFilled(false);
        user.addPlaidItem(plaidItem);
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(userRepository.getUserWithPlaidItems(42L)).thenReturn(user);

        PlaidApiException failure = new PlaidApiException(
            org.springframework.http.HttpStatus.BAD_GATEWAY,
            "API_ERROR",
            "ITEM_LOGIN_REQUIRED",
            "The Plaid connection requires attention.",
            "plaid-request-id",
            "accounts-get",
            null
        );
        doThrow(failure).when(plaidService).refreshPlaidItem(plaidItem);

        assertThatThrownBy(controller::getUserData).isSameAs(failure);
    }
}

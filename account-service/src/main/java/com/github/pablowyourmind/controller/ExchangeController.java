package com.github.pablowyourmind.controller;

import com.github.pablowyourmind.model.ExchangeInfo;
import com.github.pablowyourmind.model.ExchangeNbpData;
import com.github.pablowyourmind.model.datamodel.MainAccount;
import com.github.pablowyourmind.model.datamodel.Subaccount;
import com.github.pablowyourmind.model.types.CurrencyType;
import com.github.pablowyourmind.model.types.ExchangeType;
import com.github.pablowyourmind.persistence.SubaccountRepository;
import com.github.pablowyourmind.service.ExchangeServiceImpl;
import com.github.pablowyourmind.service.api.AccountService;
import com.github.pablowyourmind.service.api.ExchangeService;
import jakarta.validation.Valid;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pl.foltak.polishidnumbers.pesel.InvalidPeselException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    @PostMapping("/exchange")
    public ResponseEntity<MainAccount> exchangeMoney(@Valid @RequestBody ExchangeInfo exchangeInfo) throws InvalidPeselException, IllegalArgumentException, NoSuchElementException {
        Optional<MainAccount> mainAccount = exchangeService.exchangeMoney(exchangeInfo);
        return ResponseEntity.of(mainAccount);
    }
}

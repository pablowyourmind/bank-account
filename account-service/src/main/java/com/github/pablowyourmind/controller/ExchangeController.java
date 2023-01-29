package com.github.pablowyourmind.controller;

import com.github.pablowyourmind.model.ExchangeInfo;
import com.github.pablowyourmind.model.datamodel.MainAccount;
import com.github.pablowyourmind.service.api.ExchangeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.foltak.polishidnumbers.pesel.InvalidPeselException;

import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    @PostMapping(value = "/exchange", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MainAccount> exchangeMoney(@Valid @RequestBody ExchangeInfo exchangeInfo) throws InvalidPeselException, IllegalArgumentException, NoSuchElementException {
        Optional<MainAccount> mainAccount = exchangeService.exchangeMoney(exchangeInfo);
        return ResponseEntity.of(mainAccount);
    }
}

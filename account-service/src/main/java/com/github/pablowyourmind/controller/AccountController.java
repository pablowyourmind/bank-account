package com.github.pablowyourmind.controller;

import com.github.pablowyourmind.model.datamodel.MainAccount;
import com.github.pablowyourmind.model.RegistrationInfo;
import com.github.pablowyourmind.service.api.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.foltak.polishidnumbers.pesel.InvalidPeselException;

import java.io.InvalidObjectException;
import java.util.Optional;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/info")
    public ResponseEntity<MainAccount> getMainAccountInfo(@RequestParam(value = "pesel", defaultValue = "") String pesel) throws InvalidPeselException {
        Optional<MainAccount> mainAccountInfo = accountService.getMainAccountInfo(pesel);
        return ResponseEntity.of(mainAccountInfo);
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MainAccount> registerAccount(@Valid @RequestBody RegistrationInfo registrationInfo) throws InvalidPeselException, InvalidObjectException {
        MainAccount mainAccount = accountService.validateAndPersistMainAccount(registrationInfo);
        return new ResponseEntity<>(mainAccount, HttpStatus.OK);
    }
}

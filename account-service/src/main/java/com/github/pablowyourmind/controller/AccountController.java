package com.github.pablowyourmind.controller;

import com.github.pablowyourmind.model.datamodel.MainAccount;
import com.github.pablowyourmind.model.RegistrationInfo;
import com.github.pablowyourmind.model.datamodel.Subaccount;
import com.github.pablowyourmind.model.types.CurrencyType;
import com.github.pablowyourmind.persistence.MainAccountRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.foltak.polishidnumbers.pesel.InvalidPeselException;
import pl.foltak.polishidnumbers.pesel.Pesel;
import pl.foltak.polishidnumbers.pesel.PeselValidator;

import java.io.InvalidObjectException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


@RestController
public class AccountController {

    @Autowired
    private MainAccountRepository mainAccountRepository;

    @GetMapping("/info")
    public MainAccount getMainAccountInfo(@RequestParam(value = "pesel", defaultValue = "") String peselString)
            throws InvalidPeselException {

        new PeselValidator().assertIsValid(peselString);
        return mainAccountRepository.findById(peselString).orElse(null);
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MainAccount> registerAccount(@Valid @RequestBody RegistrationInfo registrationInfo) throws InvalidPeselException, InvalidObjectException {
        MainAccount mainAccount = createMainAndSubAccounts(registrationInfo);
        validateRegistrationInfo(mainAccount);
        mainAccountRepository.save(mainAccount);
        return new ResponseEntity<>(mainAccount, HttpStatus.OK);
    }

    //-------------------------------- private methods ------------------------------------------

    private MainAccount createMainAndSubAccounts(RegistrationInfo registrationInfo) {
        MainAccount mainAccount = new MainAccount(registrationInfo.getPesel(), registrationInfo.getName(), registrationInfo.getSurname());
        Subaccount plnSubaccount = new Subaccount(CurrencyType.PLN, registrationInfo.getInitialAmount());
        mainAccount.getSubaccounts().add(plnSubaccount);
        Subaccount usdSubaccount = new Subaccount(CurrencyType.USD, BigDecimal.ZERO);
        mainAccount.getSubaccounts().add(usdSubaccount);
        return mainAccount;
    }

    private void validateRegistrationInfo(MainAccount mainAccount) throws InvalidPeselException, InvalidObjectException {
        validateExistingAccount(mainAccount);
        ageValidation(mainAccount);
    }

    private void validateExistingAccount(MainAccount mainAccount) throws InvalidPeselException, InvalidObjectException {
        MainAccount existingAccountForPesel = getMainAccountInfo(mainAccount.getPesel());
        if (existingAccountForPesel != null) {
            throw new InvalidObjectException("Osoba z danym numerem PESEL istnieje w bazie.");
        }
    }

    private void ageValidation(MainAccount mainAccount) throws InvalidObjectException {
        try {
            Pesel pesel = new Pesel(mainAccount.getPesel());
            LocalDate birthDate = pesel.getBirthDate();
            LocalDate today = LocalDate.now();
            long between = ChronoUnit.YEARS.between(birthDate, today);
            if (between < 18) {
                throw new InvalidObjectException("Osoba musi być pełnoletnia");
            }
        } catch (InvalidPeselException e) {
            e.printStackTrace();
        }
    }

}

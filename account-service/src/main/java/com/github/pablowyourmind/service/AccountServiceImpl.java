package com.github.pablowyourmind.service;

import com.github.pablowyourmind.model.RegistrationInfo;
import com.github.pablowyourmind.model.datamodel.MainAccount;
import com.github.pablowyourmind.model.datamodel.Subaccount;
import com.github.pablowyourmind.model.types.CurrencyType;
import com.github.pablowyourmind.persistence.MainAccountRepository;
import com.github.pablowyourmind.service.api.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.foltak.polishidnumbers.pesel.InvalidPeselException;
import pl.foltak.polishidnumbers.pesel.Pesel;
import pl.foltak.polishidnumbers.pesel.PeselValidator;

import java.io.InvalidObjectException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private MainAccountRepository mainAccountRepository;

    @Override
    public Optional<MainAccount> getMainAccountInfo(String pesel) throws InvalidPeselException {
        new PeselValidator().assertIsValid(pesel);
        return mainAccountRepository.findById(pesel);
    }

    @Override
    public MainAccount validateAndPersistMainAccount(RegistrationInfo registrationInfo) throws InvalidPeselException, InvalidObjectException {
        MainAccount mainAccount = createMainAndSubAccounts(registrationInfo);
        validateRegistrationInfo(mainAccount);
        mainAccountRepository.save(mainAccount);
        return mainAccount;
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
        Optional<MainAccount> mainAccountInDb = getMainAccountInfo(mainAccount.getPesel());
        if (mainAccountInDb.isPresent()) {
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

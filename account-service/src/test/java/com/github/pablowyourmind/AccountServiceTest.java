package com.github.pablowyourmind;

import com.github.pablowyourmind.model.RegistrationInfo;
import com.github.pablowyourmind.model.datamodel.MainAccount;
import com.github.pablowyourmind.model.datamodel.Subaccount;
import com.github.pablowyourmind.model.types.CurrencyType;
import com.github.pablowyourmind.persistence.MainAccountRepository;
import com.github.pablowyourmind.service.api.AccountService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.foltak.polishidnumbers.pesel.InvalidPeselException;

import java.io.InvalidObjectException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private MainAccountRepository mainAccountRepository;

    String CORRECT_PESEL = "88062354132";

    @Test
    public void contextLoads() throws Exception {
        assertThat(accountService).isNotNull();
    }

    @Test
    public void getAccountInfoSuccesful() throws InvalidPeselException {

        MainAccount correctAccount = new MainAccount(CORRECT_PESEL, "Test", "Test", prepareTestSubaccounts());
        mainAccountRepository.save(correctAccount);
        assertDoesNotThrow(() -> accountService.getMainAccountInfo(CORRECT_PESEL));
        Optional<MainAccount> mainAccountFromDb = accountService.getMainAccountInfo(CORRECT_PESEL);
        assertEquals(correctAccount.getPesel(), mainAccountFromDb.get().getPesel());
        assertEquals(correctAccount.getFirstName(), mainAccountFromDb.get().getFirstName());
        assertEquals(correctAccount.getLastName(), mainAccountFromDb.get().getLastName());

        List<Subaccount> correctAccountSubaccounts = correctAccount.getSubaccounts();
        List<Subaccount> subaccountsFromDb = mainAccountFromDb.get().getSubaccounts();
        compareSubaccounts(correctAccountSubaccounts, subaccountsFromDb);
    }

    @Test
    public void getAccountInfoInvalidFailed() throws InvalidPeselException {
        String invalidPesel = "88162354132";
        MainAccount correctAccount = new MainAccount(CORRECT_PESEL, "Test", "Test", prepareTestSubaccounts());
        mainAccountRepository.save(correctAccount);
        assertThrows(InvalidPeselException.class, () -> accountService.getMainAccountInfo(invalidPesel));
    }

    @Test
    public void getAccountInfoUnknownPesel() throws InvalidPeselException {
        String unknownPesel = "90040943805";
        MainAccount correctAccount = new MainAccount(CORRECT_PESEL, "Test", "Test", prepareTestSubaccounts());
        mainAccountRepository.save(correctAccount);
        assertDoesNotThrow(() -> accountService.getMainAccountInfo(unknownPesel));
        assertEquals(Optional.empty(), accountService.getMainAccountInfo(unknownPesel));
    }

    @Test
    public void registrationSuccesful() throws InvalidPeselException, InvalidObjectException {
        mainAccountRepository.deleteAll();
        RegistrationInfo registrationInfo = new RegistrationInfo();
        registrationInfo.setName("Test");
        registrationInfo.setSurname("Test");
        BigDecimal initialAmount = BigDecimal.valueOf(500);
        registrationInfo.setInitialAmount(initialAmount);
        registrationInfo.setPesel(CORRECT_PESEL);
        assertDoesNotThrow(() -> accountService.validateAndPersistMainAccount(registrationInfo));
        Optional<MainAccount> mainAccount = mainAccountRepository.findById(CORRECT_PESEL);
        assertTrue(mainAccount.isPresent());
        List<Subaccount> subaccounts = mainAccount.get().getSubaccounts();
        assertEquals(2, subaccounts.size());
        assertTrue(subaccounts.stream().anyMatch(sa -> CurrencyType.PLN.equals(sa.getCurrency()) && initialAmount.setScale(2, RoundingMode.HALF_UP).equals(sa.getAmount())));
        assertTrue(subaccounts.stream().anyMatch(sa -> CurrencyType.USD.equals(sa.getCurrency()) && BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).equals(sa.getAmount())));
    }


    private List<Subaccount> prepareTestSubaccounts() {
        return List.of(
                new Subaccount(CurrencyType.PLN, BigDecimal.valueOf(123.00)),
                new Subaccount(CurrencyType.USD, BigDecimal.valueOf(544))
        );
    }

    private void compareSubaccounts(List<Subaccount> correctAccountSubaccounts, List<Subaccount> subaccountsFromDb) {
        assertEquals(correctAccountSubaccounts.size(), subaccountsFromDb.size());
        for (int i = 0; i < correctAccountSubaccounts.size(); i++) {
            Subaccount correctSubaccount = correctAccountSubaccounts.get(i);
            Subaccount dbSubaccount = subaccountsFromDb.get(i);
            assertEquals(correctSubaccount.getCurrency(), dbSubaccount.getCurrency());
            assertEquals(correctSubaccount.getAmount().setScale(2, RoundingMode.HALF_UP),
                    dbSubaccount.getAmount().setScale(2, RoundingMode.HALF_UP));
        }
    }

}

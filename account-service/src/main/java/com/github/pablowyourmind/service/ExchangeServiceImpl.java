package com.github.pablowyourmind.service;

import com.github.pablowyourmind.model.ExchangeInfo;
import com.github.pablowyourmind.model.ExchangeNbpData;
import com.github.pablowyourmind.model.datamodel.MainAccount;
import com.github.pablowyourmind.model.datamodel.Subaccount;
import com.github.pablowyourmind.model.types.CurrencyType;
import com.github.pablowyourmind.model.types.ExchangeType;
import com.github.pablowyourmind.persistence.SubaccountRepository;
import com.github.pablowyourmind.service.api.AccountService;
import com.github.pablowyourmind.service.api.ExchangeService;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.foltak.polishidnumbers.pesel.InvalidPeselException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ExchangeServiceImpl implements ExchangeService {

    private static final String NBP_EXCHANGE_URL = "http://api.nbp.pl/api/exchangerates/rates/c/{foreignCurrencyCode}/";

    @Autowired
    private AccountService accountService;
    @Autowired
    private SubaccountRepository subaccountRepository;

    @Override
    public Optional<MainAccount> exchangeMoney(ExchangeInfo exchangeInfo) throws InvalidPeselException, NoSuchElementException {
        CurrencyType sourceCurrency = exchangeInfo.getSourceCurrency();
        CurrencyType targetCurrency = exchangeInfo.getTargetCurrency();
        Money sourceMoney = Money.of(exchangeInfo.getHowMuch(), sourceCurrency.name());

        ExchangeType exchangeType = determineExchangetype(sourceCurrency, targetCurrency);
        ExchangeNbpData exchangeInfoFromNbp = getExchangeInfoFromNbp(sourceCurrency, targetCurrency, exchangeType);


        Optional<MainAccount> mainAccount = getMainAccount(exchangeInfo.getPesel());
        Subaccount sourceCurrencyAccount = getSubaccount(sourceCurrency, mainAccount);
        Subaccount targetCurrencyAccount = getSubaccount(targetCurrency, mainAccount);

        validateHowMuch(exchangeInfo, sourceCurrencyAccount);

        if (ExchangeType.NONE.equals(exchangeType)) {
            return mainAccount;
        } else if (!exchangeInfoFromNbp.getRates().isEmpty()) {
            exchangeAmountsAndUpdateAccounts(exchangeInfo, sourceMoney, exchangeType, exchangeInfoFromNbp, sourceCurrencyAccount, targetCurrencyAccount);
        }
        subaccountRepository.save(sourceCurrencyAccount);
        subaccountRepository.save(targetCurrencyAccount);
        return mainAccount;
    }

    private void exchangeAmountsAndUpdateAccounts(ExchangeInfo exchangeInfo, Money sourceMoney, ExchangeType exchangeType, ExchangeNbpData exchangeInfoFromNbp, Subaccount sourceCurrencyAccount, Subaccount targetCurrencyAccount) {
        BigDecimal newSourceAmount = sourceCurrencyAccount.getAmount().subtract(exchangeInfo.getHowMuch());
        if (ExchangeType.BUY.equals(exchangeType)) {
            double ask = exchangeInfoFromNbp.getRates().get(0).getAsk();
            BigDecimal exchangedMoney = sourceMoney.divide(ask).getNumberStripped().setScale(2, RoundingMode.HALF_UP);
            updateSubaccounts(sourceCurrencyAccount, targetCurrencyAccount, newSourceAmount, exchangedMoney);
        } else {
            double bid = exchangeInfoFromNbp.getRates().get(0).getBid();
            BigDecimal exchangedMoney = sourceMoney.multiply(bid).getNumberStripped().setScale(2, RoundingMode.HALF_UP);
            updateSubaccounts(sourceCurrencyAccount, targetCurrencyAccount, newSourceAmount, exchangedMoney);
        }
    }

    private void updateSubaccounts(Subaccount sourceCurrencyAccount, Subaccount targetCurrencyAccount, BigDecimal newSourceAmount, BigDecimal exchangedMoney) {
        BigDecimal amountInTargetCurrency = targetCurrencyAccount.getAmount().add(exchangedMoney);
        sourceCurrencyAccount.setAmount(newSourceAmount);
        targetCurrencyAccount.setAmount(amountInTargetCurrency);
    }

    private void validateHowMuch(ExchangeInfo exchangeInfo, Subaccount sourceCurrencyAccount) {
        BigDecimal amount = sourceCurrencyAccount.getAmount();
        if (amount.compareTo(exchangeInfo.getHowMuch()) < 0) {
            throw new IllegalArgumentException("Brak wystarczającej ilości pieniędzy do wymiany");
        }
    }

    private ExchangeType determineExchangetype(CurrencyType sourceCurrency, CurrencyType targetCurrency) {
        if (sourceCurrency.equals(targetCurrency)) {
            return ExchangeType.NONE;
        } else if (CurrencyType.PLN.equals(sourceCurrency)) {
            return ExchangeType.BUY;
        } else {
            return ExchangeType.SELL;
        }
    }

    private ExchangeNbpData getExchangeInfoFromNbp(CurrencyType sourceCurrency, CurrencyType targetCurrency, ExchangeType exchangeType) throws NoSuchElementException {
        CurrencyType currencyToAsk = ExchangeType.BUY.equals(exchangeType) ? targetCurrency : sourceCurrency;
        ExchangeNbpData exchangeNbpInfoFromNbp = fetchExchangeInfoFromNbp(currencyToAsk);
        if (exchangeNbpInfoFromNbp == null) {
            throw new NoSuchElementException("No exchange information fetched from NBP");
        }
        return exchangeNbpInfoFromNbp;
    }

    private ExchangeNbpData fetchExchangeInfoFromNbp(CurrencyType targetCurrency) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(NBP_EXCHANGE_URL, ExchangeNbpData.class, targetCurrency);
    }

    private Optional<MainAccount> getMainAccount(String pesel) throws InvalidPeselException {
        return accountService.getMainAccountInfo(pesel);
    }

    private Subaccount getSubaccount(CurrencyType sourceCurrency, Optional<MainAccount> mainAccount) {
        List<Subaccount> subaccounts = mainAccount.map(MainAccount::getSubaccounts).orElse(new ArrayList<>());
        return subaccounts
                .stream()
                .filter(subaccount -> subaccount.getCurrency().equals(sourceCurrency))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No subaccount"));
    }
}

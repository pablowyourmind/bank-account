package com.github.pablowyourmind.controller;

import com.github.pablowyourmind.model.ExchangeInfo;
import com.github.pablowyourmind.model.datamodel.MainAccount;
import com.github.pablowyourmind.model.datamodel.Subaccount;
import com.github.pablowyourmind.model.types.CurrencyType;
import com.github.pablowyourmind.persistence.MainAccountRepository;
import com.github.pablowyourmind.persistence.SubaccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class ExchangeController {

    private static final String NBP_EXCHANGE_URL = "http://api.nbp.pl/api/exchangerates/rates/c/{foreignCurrencyCode}/";

    @Autowired
    private MainAccountRepository mainAccountRepository;
    @Autowired
    private SubaccountRepository subaccountRepository;

    @GetMapping("/exchange")
    public MainAccount exchangeMoney(
            @RequestParam(value = "pesel", defaultValue = "") String pesel,
            @RequestParam(value = "ile", defaultValue = "0") Double howMuch,
            @RequestParam(value = "walutabazowa", defaultValue = "PLN") CurrencyType sourceCurrency,
            @RequestParam(value = "walutadocelowa", defaultValue = "PLN") CurrencyType targetCurrency) {

        ExchangeInfo exchangeInfoFromNbp = getExchangeInfoFromNbp(targetCurrency);
        Optional<MainAccount> mainAccount = getMainAccount(pesel);
        List<Subaccount> subaccounts = mainAccount.map(MainAccount::getSubaccounts).orElse(new ArrayList<>());
        Optional<Subaccount> sourceCurrencyAccount = subaccounts.stream().filter(subaccount -> subaccount.getCurrency().equals(sourceCurrency)).findFirst();
        Optional<Subaccount> targetCurrencyAccount = subaccounts.stream().filter(subaccount -> subaccount.getCurrency().equals(targetCurrency)).findFirst();

        if (sourceCurrency.equals(targetCurrency)) {
            return mainAccount.orElse(null);
        } else if (CurrencyType.PLN.equals(sourceCurrency)) {
            BigDecimal bigDecimal = BigDecimal.valueOf(exchangeInfoFromNbp.getRates().get(0).getAsk());
            
        }
        return new MainAccount();
    }

    private ExchangeInfo getExchangeInfoFromNbp(CurrencyType targetCurrency) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(NBP_EXCHANGE_URL, ExchangeInfo.class, targetCurrency);
    }

    private Optional<MainAccount> getMainAccount(String pesel) {
        return mainAccountRepository.findById(pesel);
    }
}

package com.github.pablowyourmind.service.api;

import com.github.pablowyourmind.model.ExchangeInfo;
import com.github.pablowyourmind.model.datamodel.MainAccount;
import pl.foltak.polishidnumbers.pesel.InvalidPeselException;

import java.util.NoSuchElementException;
import java.util.Optional;

public interface ExchangeService {

    Optional<MainAccount> exchangeMoney(ExchangeInfo exchangeInfo) throws InvalidPeselException, NoSuchElementException;
}

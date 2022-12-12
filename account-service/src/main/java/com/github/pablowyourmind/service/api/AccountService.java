package com.github.pablowyourmind.service.api;

import com.github.pablowyourmind.model.RegistrationInfo;
import com.github.pablowyourmind.model.datamodel.MainAccount;
import pl.foltak.polishidnumbers.pesel.InvalidPeselException;

import java.io.InvalidObjectException;
import java.util.Optional;

public interface AccountService {

    Optional<MainAccount> getMainAccountInfo(String pesel) throws InvalidPeselException ;

    MainAccount validateAndPersistMainAccount(RegistrationInfo registrationInfo) throws InvalidPeselException, InvalidObjectException;
}

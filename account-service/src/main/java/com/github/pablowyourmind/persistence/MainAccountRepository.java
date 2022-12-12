package com.github.pablowyourmind.persistence;

import com.github.pablowyourmind.model.datamodel.MainAccount;
import org.springframework.data.repository.CrudRepository;

public interface MainAccountRepository extends CrudRepository<MainAccount, String> {
}

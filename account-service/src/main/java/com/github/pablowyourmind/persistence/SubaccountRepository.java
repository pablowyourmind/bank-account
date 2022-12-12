package com.github.pablowyourmind.persistence;

import com.github.pablowyourmind.model.datamodel.Subaccount;
import org.springframework.data.repository.CrudRepository;

public interface SubaccountRepository extends CrudRepository<Subaccount, Long> {
}

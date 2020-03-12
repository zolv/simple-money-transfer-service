package com.testbank.money.persistence;

import com.testbank.money.di.Service;
import com.testbank.money.persistence.model.AccountEntity;

/** Marker class for type safety */
public class AccountRepository extends MapRepository<String, AccountEntity> implements Service {

  public AccountRepository() {
    super(AccountEntity::getId);
  }
}

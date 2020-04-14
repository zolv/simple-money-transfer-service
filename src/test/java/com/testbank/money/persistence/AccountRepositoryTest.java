package com.testbank.money.persistence;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.testbank.money.common.model.Currency;
import com.testbank.money.persistence.model.AccountEntity;

public class AccountRepositoryTest {

  private AccountRepository repository;

  @Before
  public void before() {
    repository = new AccountRepository();
  }

  @Test
  public void mustSaveDefaultValuesForAccount() {
    AccountEntity account = AccountEntity.builder().build();

    final AccountEntity persisted = repository.save(account);

    Assert.assertEquals(BigDecimal.ZERO, persisted.getBalance());
    Assert.assertEquals(Currency.EUR, persisted.getCurrency());
  }

  @Test
  public void mustSaveAccount() {
    AccountEntity account = AccountEntity.builder().currency(Currency.GBP).build();

    final AccountEntity persisted = repository.save(account);

    Assert.assertEquals(BigDecimal.ZERO, persisted.getBalance());
    Assert.assertEquals(Currency.GBP, persisted.getCurrency());
  }

  @Test
  public void testFindByIdentifier() {
    AccountEntity account = AccountEntity.builder().currency(Currency.GBP).build();

    final AccountEntity persisted = repository.save(account);
    final Optional<AccountEntity> found = repository.findByIdentifier(persisted.getId());

    Assert.assertTrue(found.isPresent());
    Assert.assertEquals(BigDecimal.ZERO, persisted.getBalance());
    Assert.assertEquals(Currency.GBP, persisted.getCurrency());
  }

  @Test
  public void testFindAll() {
    AccountEntity account1 = AccountEntity.builder().currency(Currency.GBP).build();
    AccountEntity account2 = AccountEntity.builder().currency(Currency.GBP).build();
    AccountEntity account3 = AccountEntity.builder().currency(Currency.GBP).build();

    final AccountEntity persisted1 = repository.save(account1);
    final AccountEntity persisted2 = repository.save(account2);
    final AccountEntity persisted3 = repository.save(account3);
    final List<AccountEntity> found = repository.findAll();

    Assert.assertEquals(3, found.size());
    Assert.assertTrue(
        found
            .stream()
            .filter(item -> item.getId().equals(persisted1.getId()))
            .findAny()
            .isPresent());

    Assert.assertTrue(
        found
            .stream()
            .filter(item -> item.getId().equals(persisted2.getId()))
            .findAny()
            .isPresent());

    Assert.assertTrue(
        found
            .stream()
            .filter(item -> item.getId().equals(persisted3.getId()))
            .findAny()
            .isPresent());
  }
}

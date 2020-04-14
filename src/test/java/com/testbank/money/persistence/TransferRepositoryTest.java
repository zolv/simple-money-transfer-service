package com.testbank.money.persistence;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.testbank.money.common.model.Currency;
import com.testbank.money.common.model.TransferStatus;
import com.testbank.money.persistence.model.TransferEntity;

public class TransferRepositoryTest {

  private TransferRepository repository;

  @Before
  public void before() {
    repository = new TransferRepository();
  }

  @Test
  public void mustSaveTransfer() {
    TransferEntity account =
        TransferEntity.builder()
            .fromAccountId(UUID.randomUUID().toString())
            .toAccountId(UUID.randomUUID().toString())
            .currency(Currency.GBP)
            .amount(new BigDecimal("12.34"))
            .status(TransferStatus.NEW)
            .build();

    final TransferEntity persisted = repository.save(account);

    Assert.assertEquals(new BigDecimal("12.34"), persisted.getAmount());
    Assert.assertEquals(Currency.GBP, persisted.getCurrency());
    Assert.assertEquals(TransferStatus.NEW, persisted.getStatus());
  }

  @Test
  public void testFindByIdentifier() {
    TransferEntity account =
        TransferEntity.builder()
            .fromAccountId(UUID.randomUUID().toString())
            .toAccountId(UUID.randomUUID().toString())
            .currency(Currency.GBP)
            .amount(new BigDecimal("12.34"))
            .status(TransferStatus.NEW)
            .build();

    final TransferEntity persisted = repository.save(account);
    final Optional<TransferEntity> found = repository.findByIdentifier(persisted.getId());

    Assert.assertTrue(found.isPresent());
    Assert.assertEquals(new BigDecimal("12.34"), persisted.getAmount());
    Assert.assertEquals(Currency.GBP, persisted.getCurrency());
    Assert.assertEquals(TransferStatus.NEW, persisted.getStatus());
  }

  @Test
  public void testFindAll() {
    TransferEntity account1 = TransferEntity.builder().currency(Currency.GBP).build();
    TransferEntity account2 = TransferEntity.builder().currency(Currency.GBP).build();
    TransferEntity account3 = TransferEntity.builder().currency(Currency.GBP).build();

    final TransferEntity persisted1 = repository.save(account1);
    final TransferEntity persisted2 = repository.save(account2);
    final TransferEntity persisted3 = repository.save(account3);
    final List<TransferEntity> found = repository.findAll();

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

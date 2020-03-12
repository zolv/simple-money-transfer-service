package com.testbank.money.persistence.model;

import java.math.BigDecimal;

import com.testbank.money.common.model.Currency;
import com.testbank.money.common.model.TransferStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class TransferEntity extends BaseEntity {

  private final String fromAccountId;
  private final String toAccountId;
  private final BigDecimal amount;
  private final Currency currency;
  private TransferStatus status;
}

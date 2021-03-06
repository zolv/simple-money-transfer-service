package com.testbank.money.domain.model;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.testbank.money.common.model.Currency;
import com.testbank.money.common.model.TransferStatus;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class Transfer extends BaseEntity {
  @NotNull private String fromAccountId;
  @NotNull private String toAccountId;

  @Min(1L)
  private BigDecimal amount;

  @NotNull private @Default Currency currency = Currency.EUR;
  private @Default TransferStatus status = TransferStatus.NEW;
}

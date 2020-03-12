package com.testbank.money.api.model;

import java.math.BigDecimal;

import com.testbank.money.common.model.Currency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** No {@link Builder} usage due to JSON serialization. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

  private String fromAccountId;
  private String toAccountId;
  private BigDecimal amount;
  private Currency currency;
}

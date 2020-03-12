package com.testbank.money.api.model;

import java.math.BigDecimal;

import com.testbank.money.common.model.Currency;
import com.testbank.money.common.model.TransferStatus;

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
public class TransferResponse {

  private String id;
  private String fromAccountId;
  private String toAccountId;
  private BigDecimal amount;
  private Currency currency;
  private TransferStatus status;
}

package com.testbank.money.api.model;

import java.math.BigDecimal;

import javax.validation.Valid;

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
@Valid
public class AccountRequest {

  private Currency currency = Currency.EUR;
  private BigDecimal balance = BigDecimal.ZERO;
}

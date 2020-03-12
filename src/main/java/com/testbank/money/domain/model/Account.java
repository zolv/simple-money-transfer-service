package com.testbank.money.domain.model;

import java.math.BigDecimal;

import javax.validation.Valid;

import com.testbank.money.common.model.Currency;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Valid
@SuperBuilder
public class Account extends BaseEntity {

  private @Default final Currency currency = Currency.EUR;

  private @Default BigDecimal balance = BigDecimal.ZERO;
}

package com.testbank.money.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.testbank.money.common.exception.UnsupportedExchange;
import com.testbank.money.common.model.Currency;
import com.testbank.money.di.Service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExchangeRateService implements Service {

  @Builder
  @Getter
  @EqualsAndHashCode
  private static class CurrencyPair {
    private final Currency from;
    private final Currency to;
  }

  private final Map<CurrencyPair, BigDecimal> ratios = new HashMap<>(6);

  public BigDecimal exchangeTo(Currency fromCurrency, BigDecimal fromAmount, Currency toCurrency)
      throws UnsupportedExchange {

    final BigDecimal result;
    if (fromCurrency.equals(toCurrency)) {
      /*
       * Assume the ratio is 1.00
       */
      result = fromAmount;
    } else {
      final CurrencyPair pairLookup =
          CurrencyPair.builder().from(fromCurrency).to(toCurrency).build();
      final BigDecimal ratio;
      if ((ratio = ratios.get(pairLookup)) != null) {
        return fromAmount.multiply(ratio);
      } else {
        throw new UnsupportedExchange(fromCurrency, toCurrency);
      }
    }
    return result;
  }

  public ExchangeRateService() {
    super();
    /*
     * For sake of simplicity, we support exchange rations only between EUR, USD and GBP.
     * We can support all of currencies from ISO 4217 even having a separated service for that.
     */
    ratios.put(
        CurrencyPair.builder().from(Currency.EUR).to(Currency.USD).build(), new BigDecimal("1.14"));
    ratios.put(
        CurrencyPair.builder().from(Currency.USD).to(Currency.EUR).build(), new BigDecimal("0.88"));

    ratios.put(
        CurrencyPair.builder().from(Currency.EUR).to(Currency.GBP).build(), new BigDecimal("0.88"));
    ratios.put(
        CurrencyPair.builder().from(Currency.GBP).to(Currency.EUR).build(), new BigDecimal("1.14"));

    ratios.put(
        CurrencyPair.builder().from(Currency.USD).to(Currency.GBP).build(), new BigDecimal("0.77"));
    ratios.put(
        CurrencyPair.builder().from(Currency.GBP).to(Currency.USD).build(), new BigDecimal("11.3"));
  }
}

package com.testbank.money.persistence;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

public interface Repository<I, T> {

  @NotNull
  T save(@NotNull T entity);

  Optional<T> findByIdentifier(@NotNull I identifier);

  @NotNull
  List<T> findAll();
}

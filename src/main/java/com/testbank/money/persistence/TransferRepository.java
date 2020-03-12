package com.testbank.money.persistence;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.testbank.money.common.model.TransferStatus;
import com.testbank.money.di.Service;
import com.testbank.money.persistence.model.TransferEntity;

/** Marker class for type safety */
public class TransferRepository extends MapRepository<String, TransferEntity> implements Service {

  public TransferRepository() {
    super(TransferEntity::getId);
  }

  public Collection<? extends TransferEntity> findByStatus(TransferStatus status) {
    synchronized (this.repository) {
      return this.repository
          .entrySet()
          .stream()
          .map(Map.Entry::getValue)
          .filter(transfer -> status.equals(transfer.getStatus()))
          .collect(Collectors.toList());
    }
  }
}

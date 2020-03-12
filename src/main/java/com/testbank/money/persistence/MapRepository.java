package com.testbank.money.persistence;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.junit.runner.notification.RunListener.ThreadSafe;

import com.testbank.money.persistence.model.BaseEntity;

@ThreadSafe
public class MapRepository<I, T extends BaseEntity> implements Repository<I, T> {

  protected final Map<I, T> repository = Collections.synchronizedMap(new HashMap<>());

  /**
   * Supplied by sub-class by constructor call. Another solution would be to create an abstract
   * method e.g "getIdProvider()" and implement it in subclasses. Both solutions have pros and cons.
   */
  protected final Function<T, I> idProvider;

  public MapRepository(@NotNull Function<T, I> idProvider) {
    super();
    this.idProvider = idProvider;
  }

  public T save(T entity) {
    entity.setId(UUID.randomUUID().toString());

    /*
     * We need cloning mechanism to support immutability.
     */
    repository.put(idProvider.apply(entity), entity);
    return entity;
  }

  public Optional<T> findByIdentifier(I identifier) {
    return Optional.ofNullable(repository.get(identifier));
  }

  public T getByIdentifier(I identifier) {
    return findByIdentifier(identifier).orElseThrow();
  }

  @Override
  public List<T> findAll() {
    /*
     * Iterating needs synchronization even if the map is synchronized map.
     */
    synchronized (repository) {
      return repository.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }
  }
}

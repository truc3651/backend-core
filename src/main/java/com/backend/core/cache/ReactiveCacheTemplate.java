package com.backend.core.cache;

import java.time.Duration;
import java.util.function.Function;

import org.springframework.data.redis.core.ReactiveRedisTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
public class ReactiveCacheTemplate<T> {
  private final ReactiveRedisTemplate<String, String> redis;
  private final ObjectMapper mapper;
  private final String keyPrefix;
  private final Duration ttl;
  private final TypeReference<T> type;

  public Mono<T> get(String id, Function<String, Mono<T>> dbFallback) {
    String key = keyPrefix + id;
    return redis
        .opsForValue()
        .get(key)
        .flatMap(this::deserialize)
        .switchIfEmpty(
            Mono.defer(
                () -> dbFallback.apply(id).flatMap(entity -> put(id, entity).thenReturn(entity))))
        .onErrorResume(
            e -> {
              log.warn("Cache read failed for key={}, falling back to DB", key, e);
              return dbFallback.apply(id);
            });
  }

  public Mono<Boolean> put(String id, T entity) {
    String key = keyPrefix + id;
    return serialize(entity)
        .flatMap(json -> redis.opsForValue().set(key, json, ttl))
        .onErrorResume(
            e -> {
              log.warn("Cache put failed for key={}", key, e);
              return Mono.just(false);
            });
  }

  public Mono<Void> evict(String id) {
    String key = keyPrefix + id;
    return redis
        .delete(key)
        .doOnError(e -> log.error("Cache evict failed for key={}", key, e))
        .onErrorResume(e -> Mono.just(0L))
        .then();
  }

  private Mono<T> deserialize(String json) {
    return Mono.fromCallable(() -> mapper.readValue(json, type));
  }

  private Mono<String> serialize(T entity) {
    return Mono.fromCallable(() -> mapper.writeValueAsString(entity));
  }
}

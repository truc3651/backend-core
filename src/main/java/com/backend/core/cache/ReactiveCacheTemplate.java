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

  public Mono<T> get(Long id, Function<Long, Mono<T>> dbFallback) {
    String key = getKey(id);
    return redis
        .opsForValue()
        .get(key)
        .flatMap(this::deserialize)
        .switchIfEmpty(
            Mono.defer(
                () -> dbFallback.apply(id).flatMap(entity -> put(id, entity).thenReturn(entity))))
        .doOnError(e -> log.warn("Cache read failed for key={}, falling back to DB", key, e))
        .onErrorResume(e -> dbFallback.apply(id));
  }

  public Mono<Boolean> put(Long id, T entity) {
    String key = getKey(id);
    return serialize(entity)
        .flatMap(json -> redis.opsForValue().set(key, json, ttl))
        .doOnError(e -> log.warn("Cache put failed for key={}", key, e))
        .onErrorResume(e -> Mono.just(false));
  }

  public Mono<Void> evict(Long id) {
    String key = getKey(id);
    return redis
        .delete(key)
        .doOnError(e -> log.error("Cache evict failed for key={}", key, e))
        .then();
  }

  private String getKey(Long id) {
    return keyPrefix + id;
  }

  private Mono<T> deserialize(String json) {
    return Mono.fromCallable(() -> mapper.readValue(json, type));
  }

  private Mono<String> serialize(T entity) {
    return Mono.fromCallable(() -> mapper.writeValueAsString(entity));
  }
}

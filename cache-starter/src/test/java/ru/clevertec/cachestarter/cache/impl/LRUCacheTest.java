package ru.clevertec.cachestarter.cache.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.clevertec.cachestarter.cache.Cache;
import ru.clevertec.cachestarter.data.Entity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LRUCacheTest {

    private static Cache<Integer, Entity> cache;
    private static int capacity;

    @BeforeAll
    static void init() {
        capacity = 3;
        cache = new LRUCache<>(capacity);
        List.of(
                        Entity.builder().arg0(1).build(),
                        Entity.builder().arg0(2).build(),
                        Entity.builder().arg0(3).build())
                .forEach(e -> cache.put(e.getArg0(), e));
    }

    @Test
    void checkGetCapacityShouldReturnCapacity() {
        int expected = capacity;
        int actual = cache.getCapacity();
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"3,0", "4,1", "2,0", "5,3", "1,4"})
    void checkCacheShouldStoreCertainElements(int nextId, int remoteId) {
        Entity nextEntity = Entity.builder().arg0(nextId).build();
        cache.put(nextId, nextEntity);
        Entity remoteEntity = cache.get(remoteId);
        assertThat(remoteEntity).isNull();
    }

    @Test
    void delete() {
        Entity entity = Entity.builder().arg0(1).build();
        cache.put(entity.getArg0(), entity);
        boolean exist = cache.get(entity.getArg0()) == null;
        assertThat(exist).isFalse();
        cache.delete(entity.getArg0());
        exist = cache.get(entity.getArg0()) == null;
        assertThat(exist).isTrue();
    }

    @Test
    void checkClear() {
        var cache = new LRUCache<>(capacity);
        List.of(Entity.builder().arg0(1).build(), Entity.builder().arg0(2).build())
                .forEach(e -> cache.put(e.getArg0(), e));
        var headBefore = cache.getHead();
        var tailBefore = cache.getTail();
        cache.clear();
        assertAll(
                () -> assertThat(cache.getMap()).isEmpty(),
                () -> assertThat(cache.getHead()).isNotEqualTo(headBefore),
                () -> assertThat(cache.getTail()).isNotEqualTo(tailBefore),
                () -> assertThat(cache.getSize()).isEqualTo(0));
    }

}
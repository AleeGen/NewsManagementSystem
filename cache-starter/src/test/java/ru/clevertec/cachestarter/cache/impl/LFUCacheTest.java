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

class LFUCacheTest {

    private static Cache<Integer, Entity> cache;
    private static int capacity;

    @BeforeAll
    static void init() {
        capacity = 3;
        cache = new LFUCache<>(capacity);
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
    @CsvSource(value = {"3,0", "4,1", "2,0", "5,4", "1,5"})
    void checkCacheShouldStoreCertainElements(int nextId, int remoteId) {
        Entity nextEntity = Entity.builder().arg0(nextId).build();
        cache.put(nextId, nextEntity);
        Entity remoteEntity = cache.get(remoteId);
        assertThat(remoteEntity).isNull();
    }

    @Test
    void checkDelete() {
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
        var cache = new LFUCache<>(capacity);
        List.of(Entity.builder().arg0(1).build(), Entity.builder().arg0(2).build())
                .forEach(e -> cache.put(e.getArg0(), e));
        cache.clear();
        assertAll(
                () -> assertThat(cache.getFreqMap().size()).isEqualTo(1),
                () -> assertThat(cache.getKeyFreqMap()).isEmpty(),
                () -> assertThat(cache.getKeyValueMap()).isEmpty(),
                () -> assertThat(cache.getMin()).isEqualTo(0));
    }

}
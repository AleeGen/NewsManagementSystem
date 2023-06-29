package ru.clevertec.cachestarter.data;

import ru.clevertec.cachestarter.annotation.CacheMethod;
import ru.clevertec.cachestarter.annotation.Caching;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ServiceImpl implements Service {

    private final Map<Integer, Entity> entities = new HashMap<>();

    @Override
    public Map<Integer, Entity> getEntities() {
        return entities;
    }

    @Caching(key = "arg0", method = CacheMethod.EXTRACT)
    public List<EntityDto> methodEXTRACT(int quantity) {
        return IntStream.range(1, quantity + 1).mapToObj(i ->
                returnDto(entities.get(i).getArg0())).toList();
    }

    @Override
    @Caching(key = "arg0")
    public EntityDto methodGET(int arg0) {
        return returnDto(arg0);
    }

    @Override
    @Caching(key = "arg0", method = CacheMethod.CHANGE)
    public EntityDto methodCHANGE(int arg0) {
        return returnDto(arg0);
    }

    @Override
    @Caching(key = "arg0", method = CacheMethod.DELETE)
    public EntityDto methodDELETE(int arg0) {
        return returnDto(arg0);
    }

    @Override
    @Caching(key = "arg0", incoming = false)
    public EntityDto incomingFALSE(int arg0) {
        return returnDto(arg0);
    }

    @Override
    @Caching(key = "arg0.arg0")
    public EntityDto nestedKeyIncomingTRUE(Entity arg0) {
        return returnDto(arg0.getArg0());
    }

    @Override
    @Caching(key = "arg2.arg0", incoming = false)
    public EntityDto nestedKeyIncomingFALSE(int arg0) {
        return returnDto(arg0);
    }

    private EntityDto returnDto(int arg0) {
        Entity entity = entities.get(arg0);
        return new EntityDto(entity.getArg0(), entity.getArg1());
    }

}
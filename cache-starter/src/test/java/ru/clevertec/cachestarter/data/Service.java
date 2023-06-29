package ru.clevertec.cachestarter.data;

import java.util.List;
import java.util.Map;

public interface Service {

    Map<Integer, Entity> getEntities();

    List<EntityDto> methodEXTRACT(int size);

    EntityDto methodGET(int arg0);

    EntityDto methodCHANGE(int arg0);

    EntityDto methodDELETE(int arg0);

    EntityDto incomingFALSE(int arg0);

    EntityDto nestedKeyIncomingTRUE(Entity arg0);

    EntityDto nestedKeyIncomingFALSE(int arg0);

}
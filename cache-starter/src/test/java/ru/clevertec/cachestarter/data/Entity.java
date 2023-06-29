package ru.clevertec.cachestarter.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Entity {

    private Integer arg0;
    private Nested arg1;

    @AllArgsConstructor
    public static class Nested {
        private Integer arg0;
    }

}
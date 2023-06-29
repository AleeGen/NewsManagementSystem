package ru.clevertec.news.data.builder.impl.dto.patch;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.data.builder.EntityBuilder;
import ru.clevertec.news.util.patch.PatchRequest;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "patchReq")
public class PatchReqBuilder implements EntityBuilder<PatchRequest> {

    private String field = "field";
    private String value = "value";

    @Override
    public PatchRequest build() {
        return new PatchRequest(field, value);
    }

}
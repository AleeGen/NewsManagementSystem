package ru.clevertec.news.data.builder.impl.dto.news;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.data.builder.EntityBuilder;
import ru.clevertec.news.dto.news.NewsRequest;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "newsReq")
public class NewsReqBuilder implements EntityBuilder<NewsRequest> {

    private String username = "username-0";
    private String title = "title";
    private String text = "text";

    @Override
    public NewsRequest build() {
        return NewsRequest.builder()
                .username(username)
                .title(title)
                .text(text)
                .build();
    }

}
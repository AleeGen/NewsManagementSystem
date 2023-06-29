package ru.clevertec.news.data.builder.impl.dto.news;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.data.builder.EntityBuilder;
import ru.clevertec.news.dto.news.NewsFilter;

import java.time.LocalDateTime;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "newsFilter")
public class NewsFilterBuilder implements EntityBuilder<NewsFilter> {

    private String username = null;
    private String title = null;
    private LocalDateTime time = null;
    private String text = null;

    @Override
    public NewsFilter build() {
        return NewsFilter.builder()
                .username(username)
                .title(title)
                .time(time)
                .text(text)
                .build();
    }

}
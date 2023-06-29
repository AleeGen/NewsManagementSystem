package ru.clevertec.news.data.builder.impl.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.data.builder.EntityBuilder;
import ru.clevertec.news.entity.Comment;
import ru.clevertec.news.entity.News;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "news")
public class NewsBuilder implements EntityBuilder<News> {

    private Long id = 1L;
    private String username = "username-0";
    private LocalDateTime time = LocalDateTime.of(2023, 1, 1, 0, 0, 0, 0);
    private String title = "title";
    private String text = "text";
    private List<Comment> comments = new ArrayList<>();

    @Override
    public News build() {
        return News.builder()
                .id(id)
                .username(username)
                .time(time)
                .title(title)
                .text(text)
                .comments(comments)
                .build();
    }

}
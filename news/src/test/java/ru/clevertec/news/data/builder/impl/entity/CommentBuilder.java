package ru.clevertec.news.data.builder.impl.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.data.builder.EntityBuilder;
import ru.clevertec.news.entity.Comment;
import ru.clevertec.news.entity.News;

import java.time.LocalDateTime;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "comment")
public class CommentBuilder implements EntityBuilder<Comment> {

    private Long id = 1L;
    private LocalDateTime time = LocalDateTime.of(2023, 1, 1, 0, 0, 0, 0);
    private String username = "username";
    private String text = "text";
    private News news = NewsBuilder.news().build();


    @Override
    public Comment build() {
        return Comment.builder()
                .id(id)
                .time(time)
                .username(username)
                .text(text)
                .news(news)
                .build();
    }

}
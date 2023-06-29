package ru.clevertec.news.data.builder.impl.dto.comments;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.data.builder.EntityBuilder;
import ru.clevertec.news.dto.comment.CommentFilter;

import java.time.LocalDateTime;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "commentFilter")
public class CommentFilterBuilder implements EntityBuilder<CommentFilter> {

    private Long id = null;
    private LocalDateTime time = null;
    private String username = null;
    private String text = null;
    private Long newsId = null;

    @Override
    public CommentFilter build() {
        return CommentFilter.builder()
                .id(id)
                .username(username)
                .text(text)
                .time(time)
                .newsId(newsId)
                .build();
    }

}
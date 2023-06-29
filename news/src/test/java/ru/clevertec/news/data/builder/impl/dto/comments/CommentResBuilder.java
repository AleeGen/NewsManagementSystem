package ru.clevertec.news.data.builder.impl.dto.comments;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.data.builder.EntityBuilder;
import ru.clevertec.news.dto.comment.CommentResponse;

import java.time.LocalDateTime;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "commentRes")
public class CommentResBuilder implements EntityBuilder<CommentResponse> {

    private Long id = 1L;
    private LocalDateTime time = LocalDateTime.of(2023, 1, 1, 0, 0, 0, 0);
    private String username = "username";
    private String text = "text";
    private Long newsId = 1L;

    @Override
    public CommentResponse build() {
        return CommentResponse.builder()
                .id(id)
                .time(time)
                .username(username)
                .text(text)
                .newsId(newsId)
                .build();
    }

}
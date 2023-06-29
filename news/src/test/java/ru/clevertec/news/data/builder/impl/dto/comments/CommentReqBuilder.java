package ru.clevertec.news.data.builder.impl.dto.comments;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.data.builder.EntityBuilder;
import ru.clevertec.news.dto.comment.CommentRequest;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "commentReq")
public class CommentReqBuilder implements EntityBuilder<CommentRequest> {

    private String username = "username";
    private String text = "text";
    private Long newsId = 1L;

    @Override
    public CommentRequest build() {
        return CommentRequest.builder()
                .username(username)
                .text(text)
                .newsId(newsId)
                .build();
    }

}
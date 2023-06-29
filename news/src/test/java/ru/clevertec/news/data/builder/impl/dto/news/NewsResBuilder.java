package ru.clevertec.news.data.builder.impl.dto.news;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.data.builder.EntityBuilder;
import ru.clevertec.news.dto.comment.CommentResponse;
import ru.clevertec.news.dto.news.NewsResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "newsRes")
public class NewsResBuilder implements EntityBuilder<NewsResponse> {

    private Long id = 1L;
    private String username = "username-0";
    private String title = "title";
    private LocalDateTime time = LocalDateTime.of(2023, 1, 1, 0, 0, 0, 0);
    private String text = "text";
    private List<CommentResponse> comments = new ArrayList<>();

    @Override
    public NewsResponse build() {
        return NewsResponse.builder()
                .id(id)
                .username(username)
                .title(title)
                .time(time)
                .text(text)
                .comments(comments)
                .build();
    }

}
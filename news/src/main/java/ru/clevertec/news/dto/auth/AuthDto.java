package ru.clevertec.news.dto.auth;

import java.util.Collection;

public record AuthDto(String username,
                      Collection<String> authorities) {
}
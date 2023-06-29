package ru.clevertec.news.data.reader;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorReader {

    public static String requiredAuthCommentPOST() {
        return read("post/required-auth-comment.json");
    }

    public static String accessDeniedCommentPOST() {
        return read("post/access-denied-comment.json");
    }

    public static String requiredAuthCommentPUT() {
        return read("put/required-auth-comment.json");
    }

    public static String accessDeniedCommentPUT() {
        return read("put/access-denied-comment.json");
    }

    public static String requiredAuthCommentPATCH() {
        return read("patch/required-auth-comment.json");
    }

    public static String accessDeniedCommentPATCH() {
        return read("patch/access-denied-comment.json");
    }

    public static String accessDeniedCommentDELETE() {
        return read("delete/access-denied-comment.json");
    }

    public static String requiredAuthCommentDELETE() {
        return read("delete/required-auth-comment.json");
    }

    public static String requiredAuthNewsPOST() {
        return read("post/required-auth-news.json");
    }

    public static String accessDeniedNewsPOST() {
        return read("post/access-denied-news.json");
    }

    public static String requiredAuthNewsPUT() {
        return read("put/required-auth-news.json");
    }

    public static String accessDeniedNewsPUT() {
        return read("put/access-denied-news.json");
    }

    public static String requiredAuthNewsPATCH() {
        return read("patch/required-auth-news.json");
    }

    public static String accessDeniedNewsPATCH() {
        return read("patch/access-denied-news.json");
    }

    public static String requiredAuthNewsDELETE() {
        return read("delete/required-auth-news.json");
    }

    public static String accessDeniedNewsDELETE() {
        return read("delete/access-denied-news.json");
    }

    @SneakyThrows
    private static String read(String path) {
        return new String(ClassLoader.
                getSystemResourceAsStream("data/error_expected/" + path)
                .readAllBytes());
    }

}
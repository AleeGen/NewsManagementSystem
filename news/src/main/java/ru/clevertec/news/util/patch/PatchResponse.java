package ru.clevertec.news.util.patch;

import java.lang.reflect.Field;

/**
 * An entity response for working with {@link Patch}
 *
 * @param modifiedField the field that was replaced
 * @param value         the value to be set
 */
public record PatchResponse(Field modifiedField,
                            Object value) {
}
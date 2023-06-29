package ru.clevertec.cachestarter.annotation;

public enum CacheMethod {

    /**
     * Searches in the cache, if it does not find it, it gets it from the outside
     * and fills the cache with the found value
     */
    GET,
    /**
     * Gets from outside and fills the cache with the found value (works for future GET requests)
     */
    CHANGE,
    /**
     * Deletes from the outside, then deletes from the cache
     */
    DELETE,
    /**
     * Elements are extracted from the returned object, which are put in the cache (works for future GET requests)
     */
    EXTRACT

}
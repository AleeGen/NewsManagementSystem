package ru.clevertec.cachestarter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Example:
 * <pre>    @Caching(key = "p3")
 * public Entity get(Object p1, ... Object pN);</pre>
 * The parameter <b>p3</b> will be the key for the returned <b>Entity</b>.
 * ___________________________________________________________________________________________
 * <pre>    @Caching(key = "p3.name")
 * public Entity get(Object p1, ... Object pN);</pre>
 * The <b>name</b> field of the parameter <b>p3</b> will be the key for the returned <b>Entity</b>
 * ___________________________________________________________________________________________
 * <br>
 * <pre>    @Caching(key = "id", incoming = false)
 * public Entity get(Object p1, ... Object pN);</pre>
 * The <b>id</b> field of the Entity object will be the key for the returned Entity</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Caching {

    /**
     * The name of the element that will be the key for the cache. The element can be a method parameter,
     * parameter fields, or return value fields.
     * <br>
     * Nesting of elements using the separator <b>'.'</b> is allowed.
     */
    String key();

    /**
     * Learn more about methods: {@link CacheMethod}
     */
    CacheMethod method() default CacheMethod.GET;

    /**
     * The key is formed from:
     * <br>
     * 1. true - an incoming object
     * <br>
     * 2. false - outgoing object
     */
    boolean incoming() default true;

}
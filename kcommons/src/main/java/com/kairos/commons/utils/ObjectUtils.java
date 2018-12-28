package com.kairos.commons.utils;

import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author pradeep
 * @date - 8/6/18
 */

public class ObjectUtils {




    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static boolean isCollectionEmpty(@Nullable Collection<?> collection){
        return (collection == null || collection.isEmpty());
    }

    public static boolean isCollectionNotEmpty(@Nullable Collection<?> collection){
        return !(collection == null || collection.isEmpty());
    }
    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }
    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static <T> boolean isNull(T object){
        return !Optional.ofNullable(object).isPresent();
    }
}

package com.kairos.commons.utils;

import org.springframework.lang.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * @author pradeep
 * @date - 8/6/18
 */

public class ObjectUtils {


    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor){
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static boolean isCollectionEmpty(@Nullable Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    public static boolean isCollectionNotEmpty(@Nullable Collection<?> collection) {
        return !(collection == null || collection.isEmpty());
    }

    public static <K, V> boolean isMapNotEmpty(@Nullable Map<K, V> map) {
        return !(map == null || map.isEmpty());
    }

    public static <K, V> boolean isMapEmpty(@Nullable Map<K, V> map) {
        return (map == null || map.isEmpty());
    }

    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static <T> boolean isNull(T object) {
        return !Optional.ofNullable(object).isPresent();
    }

    public static <T> T isNullOrElse(T object, T elseObject) {
        return Optional.ofNullable(object).orElse(elseObject);
    }

    public static <T> boolean isNotNull(T object) {
        return Optional.ofNullable(object).isPresent();
    }

    public static <E> HashSet<E> newHashSet(E... elements) {
        HashSet<E> set = new HashSet<>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    //Due to UnsupportedMethodException on calling add method of Arrays.asList
    public static <E> List<E> newArrayList(E... elements) {
        List<E> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }


    public static String getHoursStringByMinutes(int minutes){
        int hoursValue = minutes / 60; //since both are ints, you get an int
        int minutesValue = minutes % 60;
        return hoursValue + "." + minutesValue;
    }

    public static boolean isEquals(Object o1, Object o2) {
        if (o1 != null && o2 != null) {
            return o1.equals(o2);
        }
        return o1 == null && o2 == null;
    }


}

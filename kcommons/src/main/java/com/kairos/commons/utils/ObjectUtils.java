package com.kairos.commons.utils;

import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import org.springframework.lang.Nullable;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


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

    public static <T> void isNullOrEmptyThrowException(Collection<T> collection){
        if(isCollectionEmpty(collection)){
            throw new InvalidRequestException("Invalid Request");
        }
    }

    public static <E> HashSet<E> newHashSet(E... elements) {
        HashSet<E> set = new HashSet<>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet(E... elements) {
        LinkedHashSet<E> set = new LinkedHashSet<>(elements.length);
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

    public static <T> Set<BigInteger> getBigIntegerSet(Collection<T> objects) {
        return objects.stream().map(o -> new BigInteger((o).toString())).collect(Collectors.toSet());
    }

    public static <T> Set<Long> getLongSet(Collection<T> objects) {
        return objects.stream().map(o -> Long.valueOf(o.toString())).collect(Collectors.toSet());
    }

    public static <T> Set<LocalDate> getLocalDate(Collection<T> objects) {
        return !(ObjectUtils.isCollectionEmpty(objects)) ? objects.stream().map(o -> (o instanceof LocalDate) ? (LocalDate) o : DateUtils.asLocalDate((String) o)).collect(Collectors.toSet()) : newHashSet();
    }


    public static List<ShiftActivityDTO> mergeShiftActivity(List<ShiftActivityDTO> activities){
        if(isCollectionNotEmpty(activities)) {
            Collections.sort(activities);
            ShiftActivityDTO activityDTO = activities.get(0);
            BigInteger id = activityDTO.getActivityId();

            List<ShiftActivityDTO> mergedShiftActivityDTOS = new ArrayList<>();
            for (ShiftActivityDTO shiftActivityDTO : activities) {
                if (activityDTO.getEndDate().equals(shiftActivityDTO.getStartDate()) && activityDTO.getActivityId().equals(shiftActivityDTO.getActivityId())) {
                    activityDTO.setEndDate(shiftActivityDTO.getEndDate());
                } else if (activityDTO.getEndDate().equals(shiftActivityDTO.getStartDate()) && !activityDTO.getActivityId().equals(shiftActivityDTO.getActivityId())) {
                    mergedShiftActivityDTOS.add(activityDTO);
                    activityDTO = shiftActivityDTO;
                } else if (activityDTO.getEndDate().before(shiftActivityDTO.getStartDate())) {
                    mergedShiftActivityDTOS.add(activityDTO);
                    activityDTO = shiftActivityDTO;
                }
            }
            //to add last one
            mergedShiftActivityDTOS.add(activityDTO);
            return mergedShiftActivityDTOS;
        }
        return new ArrayList<>();
    }

    public static <T>  String getToString(T str) {
        return str == null ? null : str.toString();
    }
    public static Object [] removeNull(Collection<Object> objects){
        objects=objects.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return objects.toArray();
    }

    public static <T> List<BigInteger> getBigInteger(Collection<T> objects) {
        List<BigInteger> ids = new ArrayList<>();
        for (T object : objects) {
            String id = (object instanceof String) ? (String) object : ""+object;
            ids.add(new BigInteger(id));
        }
        return ids;
    }

    public static String getBigIntegerString(Iterator<BigInteger> iterator) {
        if (!iterator.hasNext()) {
            return "[]";
        } else {
            StringBuilder var2 = new StringBuilder();
            var2.append("['");

            while(true) {
                BigInteger var3 = iterator.next();
                var2.append(var3);
                if (!iterator.hasNext()) {
                    return var2.append("']").toString();
                }

                var2.append("','");
            }
        }
    }

    public static <T> int indexOf(Set<T> set, T element){
        List<T> list = new ArrayList<>(set);
        return list.indexOf(element);
    }

    public static List<Long> getLongValue(List<Object> objects) {
        return !(ObjectUtils.isCollectionEmpty(objects)) ? objects.stream().map(o -> ((Integer) o).longValue()).collect(Collectors.toList()) : new ArrayList<>();
    }

    public static Set<Long> getLongValueSet(List<Object> objects) {
        return !(ObjectUtils.isCollectionEmpty(objects)) ? objects.stream().map(o -> ((Integer) o).longValue()).collect(Collectors.toSet()) : new HashSet<>();
    }

    public static <G> Set<Long> getLongValueSetBySetOfObjects(Set<G> objects) {
        return isCollectionNotEmpty(objects) ? objects.stream().map(o -> Long.valueOf(o.toString())).collect(Collectors.toSet()) : new HashSet<>();
    }

    public static List<LocalDate> getLocalDate(List<Object> objects) {
        return !(ObjectUtils.isCollectionEmpty(objects)) ? objects.stream().map(o -> (o instanceof LocalDate) ? (LocalDate) o : DateUtils.asLocalDate((String) o)).collect(Collectors.toList()) : Arrays.asList(DateUtils.getStartDateOfWeek(), DateUtils.getEndDateOfWeek());
    }

    public static List<BigInteger> getBigIntegerValue(List<Object> objects) {
        return objects.stream().map(o -> new BigInteger((o).toString())).collect(Collectors.toList());
    }

    public static Set<BigInteger> getBigIntegerSet(List<Object> objects) {
        return objects.stream().map(o -> new BigInteger((o).toString())).collect(Collectors.toSet());
    }

    public static Set<DayOfWeek> getDaysOfWeeksfromString(List<Object> objects) {
        return objects.stream().map(o -> DayOfWeek.valueOf((o.toString()))).collect(Collectors.toSet());
    }

    public static <T> Set<String> getStringByList(Set<T> objects) {
        return objects.stream().map(o -> o.toString()).collect(Collectors.toSet());
    }

}

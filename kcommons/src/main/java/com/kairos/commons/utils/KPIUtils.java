package com.kairos.commons.utils;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KPIUtils {

    public static List<Long> getLongValue(List<Object> objects){
        return objects.stream().map(o -> ((Integer)o).longValue()).collect(Collectors.toList());
    }

    public static List<BigInteger> getBigIntegerValue(List<Object> objects){
        return objects.stream().map(o->new BigInteger(((Integer) o).toString())).collect(Collectors.toList());
    }

    public static Set<DayOfWeek> getDaysOfWeeksfromString(List<Object> objects){
        return objects.stream().map(o -> DayOfWeek.valueOf((o.toString()))).collect(Collectors.toSet());
    }

}

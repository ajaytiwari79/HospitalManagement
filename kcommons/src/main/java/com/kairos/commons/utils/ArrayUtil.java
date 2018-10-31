package com.kairos.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vipul on 2/11/17.
 */
public class ArrayUtil {
    private static final Logger logger = LoggerFactory.getLogger(ArrayUtil.class);

    public static List<Long> getUniqueElementWhichIsNotInFirst(List<Long> firstList, List<Long> secondList) {
        List<Long> uniqueElement=new ArrayList<Long>();
        for (int i = 0; i < secondList.size(); i++) {
            if (firstList.contains(secondList.get(i))){
                continue;
            }
            uniqueElement.add(secondList.get(i));
        }
        return uniqueElement;
    }

    public static <T> Set<T> getUnionOfList(List<T> first, List<T> second) {
        Set<T> union = new HashSet<>();
        if(first!=null)
            union.addAll(first);
        if(second!=null)
            union.addAll(second);
        return union;
    }
    /**
     * @param first
     * @param second
     * @return matchedDates
     * @Auther Pavan
     * @Desc This method will return the Matched or common dates from two sets
     */

    public static Set<LocalDate> getIntersectedDates(Set<LocalDate> first, Set<LocalDate> second) {
        Set<LocalDate> matchedDates = new HashSet<>();
        if (CollectionUtils.isEmpty(first) || CollectionUtils.isEmpty(second)) {
            return matchedDates;
        }
        for (LocalDate currentLocalDate : second) {
            if (first.contains(currentLocalDate)) {
                matchedDates.add(currentLocalDate);
            }

        }
        return matchedDates;
    }

}

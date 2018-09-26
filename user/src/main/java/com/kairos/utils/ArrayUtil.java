package com.kairos.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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

}

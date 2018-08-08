package com.kairos.utils;

import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.*;

public class ComparisonUtils {


    public static <T> Set<String> getNameListForMetadata(List<T> existingObject, Set<String> namesList) {

        if (existingObject.size() == 0) {
            return namesList;
        } else {
            Map<String, String> existingNamesMapData = new HashMap<>();
            Assert.notEmpty(existingObject, "Entity must Not be Empty");
            Assert.notEmpty(namesList, "Entity must Not be Empty");

            try {
                Class c = Class.forName(existingObject.get(0).getClass().getName());
                String methodName = "getName";

                Method getNameMethod = c.getMethod(methodName);
                for (T object : existingObject) {
                    String name = (String) getNameMethod.invoke(object);
                    existingNamesMapData.put(name.toLowerCase(), name);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            Set<String> newNamesList = new HashSet<>();
            namesList.forEach(name -> {
                if (!Optional.ofNullable(existingNamesMapData.get(name.toLowerCase())).isPresent()) {
                    newNamesList.add(name);
                }
            });
            return newNamesList;
        }
    }


}

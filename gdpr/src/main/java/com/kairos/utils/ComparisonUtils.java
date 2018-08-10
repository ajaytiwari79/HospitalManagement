package com.kairos.utils;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.*;

@Component
public class ComparisonUtils {


    @Inject
    private MongoTemplate mongoTemplate;

    public <T> Set<String> getNameListForMetadata(List<T> existingObject, Set<String> namesList) {

        if (existingObject.size() == 0) {
            return namesList;
        } else {
            Map<String, String> existingNamesMapData = new HashMap<>();
            List<String> existingNames = new ArrayList<>();

            Assert.notEmpty(existingObject, "Entity must Not be Empty");
            Assert.notEmpty(namesList, "Entity must Not be Empty");

            try {
                Class c = Class.forName(existingObject.get(0).getClass().getName());
                String methodName = "getName";

                Method getNameMethod = c.getMethod(methodName);
                for (T object : existingObject) {
                    existingNames.add((String) getNameMethod.invoke(object));
                }
            }
             catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            existingNames.forEach(s -> {
                existingNamesMapData.put(s.toLowerCase(), s);

            });
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

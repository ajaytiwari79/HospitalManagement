package com.kairos.utils;

import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("unchecked")
public class ComparisonUtils{

    private static final Logger LOGGER = LoggerFactory.getLogger(ComparisonUtils.class);

    public static <T> Set<String> getNewNameListOfMetadata(List<T> existingObject, Set<String> namesList) {

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

    public static <T> Set<String> getNewMetaDataNames(List<T> metadataDTOList, Set<String> existingMetadataNames) {
        Map<String, String> metadataNames = new HashMap<>();
        Set<String> lowerCaseNewNameList = new HashSet<>();
        try {
            if (!metadataDTOList.isEmpty()) {
                Class dtoClass = metadataDTOList.get(0).getClass();
                Set<String> lowerCaseNameList = new HashSet<>();
                for (T dto : metadataDTOList) {
                    String name = (String) new PropertyDescriptor("name", dtoClass).getReadMethod().invoke(dto);
                    String nameInLowerCase = name.toLowerCase();
                    metadataNames.put(nameInLowerCase, name);
                    lowerCaseNameList.add(nameInLowerCase);
                }
                lowerCaseNameList.removeAll(existingMetadataNames);
                lowerCaseNameList.forEach(name -> {
                    if (Optional.ofNullable(metadataNames.get(name.toLowerCase())).isPresent()) {
                        lowerCaseNewNameList.add(metadataNames.get(name.toLowerCase()));
                    }
                });
            }
        } catch (Exception ex) {
            LOGGER.error("Error in getMetadataNameListInLowerCase::" + ex.getMessage());
        }
        return lowerCaseNewNameList;
    }

}

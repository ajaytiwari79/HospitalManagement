package com.kairos.commons.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class StringTrimerConfiguration implements ConstraintValidator<EnableStringTrimer, Object> {
    @Override
    public void initialize(EnableStringTrimer constraintAnnotation) {
        //This is Override method
        System.out.println("test");
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if(field.getGenericType().equals(String.class) && !field.isAnnotationPresent(IgnoreStringTrimer.class)){
                if(Modifier.isPrivate(field.getModifiers())){
                    field.setAccessible(true);
                }

                try {
                    String value = ((String)field.get(object));
                    if(value!=null) {
                        field.set(object, value.trim());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}

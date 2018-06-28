package com.kairos.utils;


import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import java.util.*;

@Component
public class ComparisonUtils {

    public Set<String> checkForExistingObjectAndRemoveFromList (Set<String> namesList,Set<String> existingNames){

        Assert.notEmpty(existingNames,"Entity must Not be Empty");
        Assert.notEmpty(namesList,"Entity must Not be Empty");
       Map<String,String> existingNamesMapData=new HashMap<>();

       if (existingNames.size()==0)
       {
           return namesList;
       }
       existingNames.forEach(s -> {

           existingNamesMapData.put(s.toLowerCase(),s);

       });
       Set<String> newNamesList=new HashSet<>();
       namesList.forEach(name->{
           if (!Optional.ofNullable(existingNamesMapData.get(name.toLowerCase())).isPresent())
           {
               newNamesList.add(name);
           }
       });
       return newNamesList;
    }




}

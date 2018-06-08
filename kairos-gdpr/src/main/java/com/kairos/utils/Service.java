package com.kairos.utils;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.service.MongoBaseService;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Service<T extends MongoBaseEntity> extends MongoBaseService {

    private T object;

   // private E repositories;

    /*public Map<String, List<T>> createDataDisposal(Long countryId, List<T> dataDisposals, E repository) {
        *//*Map<String, List<T>> result = new HashMap<>();
        List<T> existing = new ArrayList<T>();
        List<T> newDataDisposals = new ArrayList<>();
        Set<String> names = new HashSet<>();
        if (dataDisposals.size() != 0) {
            for (T dataDisposal : dataDisposals) {
                if (!StringUtils.isBlank(dataDisposal)) {
                    names.add(dataDisposal.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }

            existing = repositories.;
            existing.forEach(item -> names.remove(item.getName()));
            if (names.size() != 0) {
                for (String name : names) {

                    T newDataDisposal = (T) new Object();
                    newDataDisposal. (name);
                    newDataDisposal.setCountryId(countryId);
                    newDataDisposals.add(newDataDisposal);

                }

                newDataDisposals = save(newDataDisposals);
            }
            result.put("existing", existing);
            result.put("new", newDataDisposals);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");*//*
        return null;


    }*/


    /*public Service(T object, E repositories) {
        this.object = object;
        this.repositories = repositories;
    }
*/
    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}

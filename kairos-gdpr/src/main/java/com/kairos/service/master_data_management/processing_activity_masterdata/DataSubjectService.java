package com.kairos.service.master_data_management.processing_activity_masterdata;


import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.DataSubject;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.DataSubjectMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.userContext.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class DataSubjectService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSubjectService.class);

    @Inject
    private DataSubjectMongoRepository dataSubjectMongoRepository;


    public Map<String, List<DataSubject>> createDataSubject(Long countryId,List<DataSubject> dataSubjects) {
        Map<String, List<DataSubject>> result = new HashMap<>();
        List<DataSubject> existing = new ArrayList<>();
        List<DataSubject> newDataSubjects = new ArrayList<>();
        if (dataSubjects.size() != 0) {
            for (DataSubject dataSubject : dataSubjects) {
                if (!StringUtils.isBlank(dataSubject.getName())) {
                    DataSubject exist = dataSubjectMongoRepository.findByName(countryId,dataSubject.getName());
                    if (Optional.ofNullable(exist).isPresent()) {
                        existing.add(exist);

                    } else {
                        DataSubject newDataSubject = new DataSubject();
                        newDataSubject.setName(dataSubject.getName());
                        newDataSubject.setCountryId(countryId);
                        newDataSubjects.add(save(newDataSubject));
                    }
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }

            result.put("existing", existing);
            result.put("new", newDataSubjects);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<DataSubject> getAllDataSubject() {
       return dataSubjectMongoRepository.findAllDataSubjects(UserContext.getCountryId());
          }


    public DataSubject getDataSubject(Long countryId,BigInteger id) {

        DataSubject exist = dataSubjectMongoRepository.findByIdAndNonDeleted(countryId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteDataSubject(BigInteger id) {

        DataSubject exist = dataSubjectMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public DataSubject updateDataSubject(BigInteger id, DataSubject dataSubject) {


        DataSubject exist = dataSubjectMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(dataSubject.getName());

            return save(exist);

        }
    }

    public List<DataSubject> getDataSubjectList(Long countryId,List<BigInteger> ids) {

        return dataSubjectMongoRepository.getDataSubjectList(countryId,ids);
    }


    public DataSubject getDataSubjectByName(Long countryId,String name) {


        if (!StringUtils.isBlank(name)) {
            DataSubject exist = dataSubjectMongoRepository.findByName(countryId,name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}






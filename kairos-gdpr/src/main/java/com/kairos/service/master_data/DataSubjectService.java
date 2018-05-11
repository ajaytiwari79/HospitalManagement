package com.kairos.service.master_data;


import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.RequestDataNull;
import com.kairos.persistance.model.master_data.DataSubject;
import com.kairos.persistance.repository.master_data.DataSubjectMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class DataSubjectService extends MongoBaseService {


    @Inject
    private DataSubjectMongoRepository dataSubjectMongoRepository;

    public DataSubject createDataSubject(DataSubject dataSubject) {
        String name = dataSubject.getName();
        DataSubject exist = dataSubjectMongoRepository.findByName(name);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("data already exist for name " + name);
        } else {
            DataSubject newDataSubject = new DataSubject();
            newDataSubject.setName(name);
            return save(newDataSubject);
        }
    }


    public List<DataSubject> getAllDataSubject() {
        List<DataSubject> result = dataSubjectMongoRepository.findAll();
        if (result.size()!=0) {
            return result;

        } else
            throw new DataNotExists("DataSource not exist please create purpose ");
    }



    public DataSubject getDataSubjectById(BigInteger id) {

        DataSubject exist = dataSubjectMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }



    public Boolean deleteDataSubjectById(BigInteger id) {

        DataSubject exist = dataSubjectMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            dataSubjectMongoRepository.delete(exist);
            return true;

        }
    }


    public DataSubject updateDataSubject(BigInteger id,DataSubject dataSubject) {
        DataSubject exist = dataSubjectMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(dataSubject.getName());
            return save(exist);

        }
    }


    public List<DataSubject> dataSubjectList(List<BigInteger> dataSubjectids) {

        if (dataSubjectids != null) {
            return dataSubjectMongoRepository.dataSubjectList(dataSubjectids);

        } else
            throw new RequestDataNull("requested DataSubject list is null");
    }




}

package com.kairos.service.data_inventory.processing_activity;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.ResponsibilityType;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeMongoRepository;
import com.kairos.response.dto.common.ResponsibilityTypeResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class OrganizationResponsibilityTypeService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationResponsibilityTypeService.class);

    @Inject
    private ResponsibilityTypeMongoRepository responsibilityTypeMongoRepository;

    @Inject
    private ComparisonUtils comparisonUtils;



    /**
     * @description this method create new ResponsibilityType if ResponsibilityType not exist with same name ,
     * and if exist then simply add  ResponsibilityType to existing list and return list ;
     * findByNamesAndCountryId()  return list of existing ResponsibilityType using collation ,used for case insensitive result
     * @param organizationId
     * @param responsibilityTypes
     * @return return map which contain list of new ResponsibilityType and list of existing ResponsibilityType if ResponsibilityType already exist
     *
     */
    public Map<String, List<ResponsibilityType>> createResponsibilityType( Long organizationId, List<ResponsibilityType> responsibilityTypes) {

        Map<String, List<ResponsibilityType>> result = new HashMap<>();
        Set<String> responsibilityTypeNames = new HashSet<>();
        if (!responsibilityTypes.isEmpty()) {
            for (ResponsibilityType responsibilityType : responsibilityTypes) {
                if (!StringUtils.isBlank(responsibilityType.getName())) {
                    responsibilityTypeNames.add(responsibilityType.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }
            List<ResponsibilityType> existing =  findAllByNameAndOrganizationId(organizationId,responsibilityTypeNames,ResponsibilityType.class);
            responsibilityTypeNames = comparisonUtils.getNameListForMetadata(existing, responsibilityTypeNames);

            List<ResponsibilityType> newResponsibilityTypes = new ArrayList<>();
            if (!responsibilityTypeNames.isEmpty()) {
                for (String name : responsibilityTypeNames) {

                    ResponsibilityType newResponsibilityType = new ResponsibilityType(name);
                    newResponsibilityType.setOrganizationId(organizationId);
                    newResponsibilityTypes.add(newResponsibilityType);

                }

                newResponsibilityTypes = responsibilityTypeMongoRepository.saveAll(getNextSequence(newResponsibilityTypes));

            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newResponsibilityTypes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    /**
     ** @param organizationId
     * @return list of ResponsibilityType
     */
    public List<ResponsibilityTypeResponseDTO> getAllResponsibilityType(Long organizationId) {
        return responsibilityTypeMongoRepository.findAllOrganizationResponsibilityTypes(organizationId);
    }

    /**
     * @throws DataNotFoundByIdException throw exception if ResponsibilityType not found for given id
     * @param organizationId
     * @param id id of ResponsibilityType
     * @return ResponsibilityType object fetch by given id
     */
    public ResponsibilityType getResponsibilityType(Long organizationId,BigInteger id) {

        ResponsibilityType exist = responsibilityTypeMongoRepository.findByOrganizationIdAndId(organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteResponsibilityType(Long organizationId,BigInteger id) {

        ResponsibilityType exist = responsibilityTypeMongoRepository.findByOrganizationIdAndId(organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }

    /***
     * @throws DuplicateDataException throw exception if ResponsibilityType data not exist for given id
     * @param organizationId
     * @param id id of ResponsibilityType
     * @param responsibilityType
     * @return ResponsibilityType updated object
     */
    public ResponsibilityType updateResponsibilityType(Long organizationId,BigInteger id, ResponsibilityType responsibilityType) {


        ResponsibilityType exist = responsibilityTypeMongoRepository.findByOrganizationIdAndName(organizationId,responsibilityType.getName());
        if (Optional.ofNullable(exist).isPresent() ) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  "+responsibilityType.getName());
        } else {
            exist=responsibilityTypeMongoRepository.findByid(id);
            exist.setName(responsibilityType.getName());
            return responsibilityTypeMongoRepository.save(getNextSequence(exist));

        }
    }

    /**
     * @throws DataNotExists throw exception if ResponsibilityType not exist for given name
     * @param organizationId
     * @param name name of ResponsibilityType
     * @return ResponsibilityType object fetch on basis of  name
     */
    public ResponsibilityType getResponsibilityTypeByName(Long organizationId,String name) {


        if (!StringUtils.isBlank(name)) {
            ResponsibilityType exist = responsibilityTypeMongoRepository.findByOrganizationIdAndName(organizationId,name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }














}

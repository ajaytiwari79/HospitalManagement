package com.kairos.service.data_inventory.data_category_element;


import com.kairos.dto.data_inventory.OrganizationDataSubjectDTO;
import com.kairos.dto.master_data.DataCategoryDTO;
import com.kairos.persistance.model.master_data.data_category_element.DataSubjectMapping;
import com.kairos.persistance.repository.master_data.data_category_element.DataSubjectMappingRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

@Service
public class OrganizationDataSubjectMappingService extends MongoBaseService {


    @Inject
    private DataSubjectMappingRepository dataSubjectMappingRepository;

    @Inject
    private ExceptionService exceptionService;


    public void createDataSubjectWithDataCategoriesAndDataElements(Long organizationId, List<OrganizationDataSubjectDTO> dataSubjectDTOS) {


        Set<String> dataSubjectNameList = new HashSet<>();
        for (OrganizationDataSubjectDTO dataSubjectDTO : dataSubjectDTOS) {
            dataSubjectNameList.addAll(dataSubjectDTO.getDataSubjectNames());
        }
        List<DataSubjectMapping> dataSubjects = dataSubjectMappingRepository.findByNamesAndUnitId(organizationId, dataSubjectNameList);
        if (!dataSubjects.isEmpty()) {
            exceptionService.duplicateDataException("message.duplicate", "Data Subject ", dataSubjects.get(0).getName());
        }

    }


    public void buildDataSubjectAndDataCategories(Long organizationId,List<OrganizationDataSubjectDTO> dataSubjectDTOS)
    {

        Map<List<DataSubjectMapping>,List<DataCategoryDTO>> dataSubjectsCorrespondingToDataCategoryList=new HashMap<>();

        for (OrganizationDataSubjectDTO dataSubjectDTO : dataSubjectDTOS) {






        }







    }

}

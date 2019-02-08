package com.kairos.service.template_type;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;

import com.kairos.persistence.model.template_type.TemplateType;
import com.kairos.persistence.repository.template_type.TemplateTypeRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class TemplateTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateTypeService.class);

    @Inject
    private TemplateTypeRepository templateTypeRepository;

    @Inject
    private ExceptionService exceptionService;

    /**
     * @param countryId
     * @param templateTypeList
     * @return list
     * @throws InvalidRequestException
     * @description Create template type. Create form will have only name field. We can create multiple template type in one go.
     * @author vikash patwal
     */
    public Map<String, List<TemplateType>> createTemplateType(Long countryId, List<TemplateType> templateTypeList) {
        Map<String, List<TemplateType>> result = new HashMap<>();
        Set<String> templateNames = new HashSet<>();
        for (TemplateType templateType : templateTypeList) {
            templateNames.add(templateType.getName());
        }
        List<String> nameInLowerCase = templateNames.stream().map(String::toLowerCase)
                .collect(Collectors.toList());

        List<TemplateType> existing = templateTypeRepository.findByCountryIdAndDeletedAndNameIn(countryId, nameInLowerCase);
        templateNames = ComparisonUtils.getNameListForMetadata(existing, templateNames);
        List<TemplateType> newDataTemplateList = new ArrayList<>();
        if (!templateNames.isEmpty()) {
            for (String name : templateNames) {

                TemplateType templateType1 = new TemplateType();
                templateType1.setName(name);
                templateType1.setCountryId(countryId);
                newDataTemplateList.add(templateType1);
            }
            newDataTemplateList = templateTypeRepository.saveAll(newDataTemplateList);
        }
        result.put(EXISTING_DATA_LIST, existing);
        result.put(NEW_DATA_LIST, newDataTemplateList);
        return result;
    }


    /**
     * @param templateId
     * @param countryId
     * @param templateType
     * @return TemplateType
     * @throws DuplicateDataException
     * @description this method is used for update template by id
     * @author vikash patwal
     */
    public TemplateType updateTemplateName(Long templateId, Long countryId, TemplateType templateType) {

        TemplateType previousTemplateType = templateTypeRepository.findByCountryIdAndName(countryId, templateType.getName());
        if (Optional.ofNullable(previousTemplateType).isPresent() && !templateId.equals(previousTemplateType.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "message.templateType", templateType.getName());
        }
        Integer resultCount = templateTypeRepository.updateMasterMetadataName(templateType.getName(), templateId, countryId);
        if (resultCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.templateType", templateId);
        } else {
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", templateId, templateType.getName());
        }
        return previousTemplateType;

    }

    /**
     * @param templateId
     * @return TemplateType
     * @throws DataNotFoundByIdException
     * @description this method is used for delete template type by id.
     * @author vikash patwal
     */
    public Boolean deleteTemplateType(Long templateId, Long countryId) {
        TemplateType templateType = templateTypeRepository.findByIdAndCountryIdAndDeletedFalse(templateId, countryId);
        if (!Optional.ofNullable(templateType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.templateType", templateId);
        }
        templateType.delete();
        templateTypeRepository.save(templateType);
        return true;

    }

    /**
     * @param countryId
     * @return List<TemplateType>
     * @description this method is used for get all template type.
     * @author vikash patwal
     */
    public List<TemplateType> getAllTemplateType(Long countryId) {
        return templateTypeRepository.getAllTemplateType(countryId);
    }
}

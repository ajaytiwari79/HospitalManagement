package com.kairos.service.template_type;

import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.template_type.TemplateType;
import com.kairos.persistance.repository.template_type.TemplateTypeMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class TemplateTypeService extends MongoBaseService {


    @Inject
    private TemplateTypeMongoRepository templateTypeRepository;

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
            if (!org.apache.commons.lang3.StringUtils.isBlank(templateType.getName())) {
                templateNames.add(templateType.getName());
            } else
                throw new InvalidRequestException("name could not be empty or null");
        }
        List<TemplateType> existing = findMetaDataByNamesAndCountryId(countryId, templateNames, TemplateType.class);
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
     * @param countryId
     * @param templateName
     * @return TemplateType
     * @throws DataNotExists
     * @description this method is used for get template by name
     * @author vikash patwal
     */
    public TemplateType getTemplateByName(Long countryId, String templateName) {
        TemplateType template = templateTypeRepository.findByTemplateNameAndIsDeleted(countryId, templateName);
        if (java.util.Optional.ofNullable(template).isPresent()) {
            return template;
        } else
            throw new DataNotExists("Template for template type ->" + templateName + " Not exists");
    }


    public TemplateType getTemplateById(BigInteger templateId, Long countryId) {
        TemplateType template = templateTypeRepository.findByIdAndNonDeleted(templateId, countryId);
        if (java.util.Optional.ofNullable(template).isPresent()) {
            return template;
        } else
            throw new DataNotExists("Template for template type ->" + templateId + " Not exists");
    }


    public List<TemplateType> getTemplateByIdsList(List<BigInteger> templateIds, Long countryId) {
        List<TemplateType> templates = templateTypeRepository.findTemplateTypeByIdsList(countryId, templateIds);
        List<BigInteger> ids = new ArrayList<>();
        templates.forEach(templateType -> {
            ids.add(templateType.getId());
        });
        templateIds.removeAll(ids);
        if (!templateIds.isEmpty()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Template type", templateIds.get(0));
        }
        return templates;

    }


    /**
     * @param id
     * @param countryId
     * @param templateType
     * @return TemplateType
     * @throws DuplicateDataException
     * @description this method is used for update template by id
     * @author vikash patwal
     */
    public TemplateType updateTemplateName(BigInteger id, Long countryId, TemplateType templateType) {

        TemplateType previousTemplateType = templateTypeRepository.findByIdAndNameDeleted(templateType.getName(), countryId);
        if (Optional.ofNullable(previousTemplateType).isPresent() && !id.equals(previousTemplateType.getId())) {
            throw new DuplicateDataException("template name exist for  " + templateType.getName());
        }
        previousTemplateType = templateTypeRepository.findByIdAndNonDeleted(id, countryId);
        previousTemplateType.setName(templateType.getName());
        templateTypeRepository.save(previousTemplateType);
        return previousTemplateType;

    }

    /**
     * @param id
     * @return TemplateType
     * @throws DataNotFoundByIdException
     * @description this method is used for delete template type by id.
     * @author vikash patwal
     */
    public Boolean deleteTemplateType(BigInteger id, Long countryId) {
        TemplateType templateType = templateTypeRepository.findByIdAndNonDeleted(id, countryId);
        if (!Optional.ofNullable(templateType).isPresent()) {
            throw new DataNotFoundByIdException("id not exist " + id);
        }
        delete(templateType);
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

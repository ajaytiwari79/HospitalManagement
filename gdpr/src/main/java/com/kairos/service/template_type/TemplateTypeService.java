package com.kairos.service.template_type;

import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.template_type.TemplateType;
import com.kairos.persistance.repository.template_type.TemplateTypeMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class TemplateTypeService extends MongoBaseService {


    @Inject
    private TemplateTypeMongoRepository templateTypeRepository;

    @Inject
    private ExceptionService exceptionService;

    /**
     * @description Create template type. Create form will have only name field. We can create multiple template type in one go.
     * @author vikash patwal
     * @param countryId
     * @param templateType
     * @throws InvalidRequestException
     * @return list
     */
    public Map<String, List<TemplateType>> createTemplateType(Long countryId, List<TemplateType> templateType) {
        Map<String, List<TemplateType>> result = new HashMap<>();
        List<TemplateType> existing = new ArrayList<>();
        List<TemplateType> newDataTemplate = new ArrayList<>();
        Set<String> templateName = new HashSet<>();
        for (TemplateType temp : templateType) {
            if (!org.apache.commons.lang3.StringUtils.isBlank(temp.getTemplateName())) {
                templateName.add(temp.getTemplateName());
            } else
                throw new InvalidRequestException("name could not be empty or null");
        }
        existing = templateTypeRepository.findByNameList(templateName);
        existing.forEach(item -> templateName.remove(item.getTemplateName()));
        if (templateName.size() != 0) {
            for (String name : templateName) {

                TemplateType templateType1 = new TemplateType();
                templateType1.setTemplateName(name);
                templateType1.setCountryId(countryId);
                newDataTemplate.add(templateType1);
            }
            newDataTemplate = save(newDataTemplate);
        }
        result.put("existing", existing);
        result.put("new", newDataTemplate);
        return result;
    }

    /**
     * @description this method is used for get template by name
     * @author vikash patwal
     * @param countryId
     * @param templateName
     * @throws DataNotExists
     * @return TemplateType
     */
    public TemplateType getTemplateByName(Long countryId,String templateName) {
        TemplateType template = templateTypeRepository.findByTemplateNameAndIsDeleted(countryId,templateName);
        if (java.util.Optional.ofNullable(template).isPresent()) {
            return template;
        } else
            throw new DataNotExists("Template for template type ->" + templateName + " Not exists");
    }


    public TemplateType getTemplateByById(BigInteger templateId,Long countryId) {
        TemplateType template = templateTypeRepository.findByIdAndNonDeleted(templateId,countryId);
        if (java.util.Optional.ofNullable(template).isPresent()) {
            return template;
        } else
            throw new DataNotExists("Template for template type ->" + templateId + " Not exists");
    }


    /**
     * @description this method is used for update template by id
     * @author vikash patwal
     * @param id
     * @param countryId
     * @param templateType
     * @throws DuplicateDataException
     * @return TemplateType
     */
    public TemplateType updateTemplateName(BigInteger id,Long countryId,TemplateType templateType) {

        TemplateType exists = templateTypeRepository.findByIdAndNameDeleted(templateType.getTemplateName(),countryId);
        if (Optional.ofNullable(exists).isPresent() && !id.equals(exists.getId())) {
            throw  new DuplicateDataException("template name exist for  "+templateType.getTemplateName());
        }
        exists=templateTypeRepository.findByIdAndNonDeleted(id,countryId);
        exists.setTemplateName(templateType.getTemplateName());
        save(exists);
        return exists;

    }

    /**
     * @description this method is used for delete template type by id.
     * @author vikash patwal
     * @param id
     * @throws DataNotFoundByIdException
     * @return TemplateType
     */
    public Boolean deleteTemplateType(BigInteger id,Long countryId) {
        TemplateType exists = templateTypeRepository.findByIdAndNonDeleted(id,countryId);
        if (!Optional.ofNullable(exists).isPresent()) {
            throw  new DataNotFoundByIdException("id not exist "+id);
        }
        exists.setDeleted(true);
        save(exists);
        return true;

    }

    /**
     * @description this method is used for get all template type.
     * @author vikash patwal
     * @param countryId
     * @return List<TemplateType>
     */
    public List<TemplateType> getAllTemplateType(Long countryId) {
        return templateTypeRepository.getAllTemplateType(countryId);
    }
}

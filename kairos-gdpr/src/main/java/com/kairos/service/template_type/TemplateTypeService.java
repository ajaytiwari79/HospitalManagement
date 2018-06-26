package com.kairos.service.template_type;

import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.dto.TemplateDTO;
import com.kairos.persistance.model.account_type.AccountType;
import com.kairos.persistance.model.template_type.TemplateType;
import com.kairos.persistance.repository.template_type.TemplateTypeMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.lang.StringUtils;
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



    public List<Object> createTemplateType(Long countryId,List<TemplateType> templateData) {
        List<Object> allData=new ArrayList<Object>();
           for (TemplateType data : templateData) {
                   TemplateType exists = templateTypeRepository.findByTemplateName(data.getTemplateName());
                   if (java.util.Optional.ofNullable(exists).isPresent()) {
                       Map<String, String> result = new HashMap<String, String>();
                       result.put("error","template name "+data.getTemplateName()+" already exits");
                       allData.add(result);
                   } else {
                       TemplateType templateType = new TemplateType();
                       templateType.setTemplateName(data.getTemplateName());
                       save(templateType);
                       allData.add(templateType);
                   }
           }
           return allData;
    }


    public TemplateType getTemplateByName(Long countryId,String templateName) {
        TemplateType template = templateTypeRepository.findByTemplateNameAndIsDeleted(templateName);
        if (java.util.Optional.ofNullable(template).isPresent()) {
            return template;
        } else
            throw new DataNotExists("Template for template type ->" + templateName + " Not exists");
    }


    public TemplateType updateTemplateName(BigInteger id,Long countryId,TemplateType templateType) {

        TemplateType exists = templateTypeRepository.findByIdAndNameDeleted(templateType.getTemplateName());
        if (Optional.ofNullable(exists).isPresent()&&!id.equals(exists.getId())) {
            throw  new DuplicateDataException("template name exist for  "+templateType.getTemplateName());
        }
        exists=templateTypeRepository.findByIdAndNonDeleted(id);
        exists.setTemplateName(templateType.getTemplateName());
        return exists;

    }


    public Boolean deleteTemplateType(BigInteger id) {
        TemplateType exists = templateTypeRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exists).isPresent()) {
            throw  new DataNotFoundByIdException("id not exist "+id);
        }
        exists.setDeleted(true);
        save(exists);
        return true;

    }

}

package com.kairos.service.country.tag;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.EmploymentType;
import com.kairos.persistence.model.user.country.tag.Tag;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.TagGraphRepository;
import com.kairos.response.dto.web.tag.TagDTO;
import com.kairos.service.UserBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Date;

/**
 * Created by prerna on 10/11/17.
 */
public class TagService extends UserBaseService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private TagGraphRepository tagGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    public Tag addCountryTag(Long countryId, TagDTO tagDTO) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        return tagGraphRepository.addCountryTag(tagDTO.getName(), countryId, tagDTO.getMasterDataType(), new Date().getTime(), new Date().getTime() );
    }

    public Tag updateCountryTag(Long countryId, TagDTO tagDTO, Long tagId) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        return tagGraphRepository.updateCountryTag(tagId, countryId, tagDTO.getName(), new Date().getTime());
    }

    public boolean deleteCountryTag(Long countryId, Long tagId, MasterDataTypeEnum masterDataTypeEnum){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        Tag tag = tagGraphRepository.getCountryTag(countryId, tagId, masterDataTypeEnum, false);
        if( tag == null) {
            throw new DataNotFoundByIdException("Incorrect tag id " + tagId);
        }
        tag.setDeleted(true);
        save(tag);
        return true;
    }

    public Tag addOrganizationTag(Long organizationId, TagDTO tagDTO) {
        return tagGraphRepository.addOrganizationTag(tagDTO.getName(), organizationId, tagDTO.getMasterDataType(), new Date().getTime(), new Date().getTime() );
    }

    public Tag updateOrganizationTag(Long organizationId, TagDTO tagDTO, Long tagId) {
        return tagGraphRepository.updateOrganizationTag(tagId, organizationId, tagDTO.getName(), new Date().getTime());
    }

    public boolean deleteOrganizationTag(Long orgId, Long tagId, MasterDataTypeEnum masterDataTypeEnum){
        Organization organization = organizationGraphRepository.findOne(orgId, 0);
        if (organization == null) {
            throw new DataNotFoundByIdException("Incorrect Unit Id " + orgId);
        }
        Tag tag = tagGraphRepository.getOrganizationTag(tagId, orgId, masterDataTypeEnum, false);
        if( tag == null) {
            throw new DataNotFoundByIdException("Incorrect tag id " + tagId);
        }
        tag.setDeleted(true);
        save(tag);
        return true;
    }

    public boolean updateShowCountryTagSettingOfOrganization(Long organizationId, boolean showCountryTags) {
        Organization organization = organizationGraphRepository.findOne(organizationId, 0);
        if (organization == null) {
            throw new DataNotFoundByIdException("Incorrect Unit Id " + organizationId);
        }
        organization.setShowCountryTags(showCountryTags);
        organizationGraphRepository.save(organization);
        return showCountryTags;
    }




//    public
}


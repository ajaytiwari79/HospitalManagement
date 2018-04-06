package com.kairos.service.country;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DataNotMatchedException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.user.country.CompanyCategory;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.dto.CompanyCategoryResponseDTO;
import com.kairos.persistence.repository.user.country.CompanyCategoryGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.response.dto.web.company_category.CompanyCategoryDTO;
import com.kairos.service.UserBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Created by pavan on 6/4/18.
 */
@Service
@Transactional
public class CompanyCategoryService extends UserBaseService {
    @Inject
    CountryGraphRepository countryGraphRepository;
    @Inject
    CompanyCategoryGraphRepository companyCategoryGraphRepository;

    public CompanyCategoryDTO createCompanyCategory(Long countryId, CompanyCategoryDTO companyCategoryDTO) {
        if (companyCategoryDTO.getName().trim().isEmpty()) {
            throw new DataNotMatchedException("Name can't be blank");
        }
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Country not found: " + countryId);
        }
        boolean isAlreadyExists = companyCategoryGraphRepository.findByNameExcludingCurrent(countryId, -1L, "(?i)" + companyCategoryDTO.getName().trim());
        if (isAlreadyExists) {
            throw new DuplicateDataException("CompanyCategory already exists: " + companyCategoryDTO.getName().trim());
        }
        CompanyCategory companyCategory = new CompanyCategory(companyCategoryDTO.getName().trim(), companyCategoryDTO.getDescription(), country);
        save(companyCategory);
        return new CompanyCategoryDTO(companyCategory.getId(), companyCategory.getName(), companyCategory.getDescription());
    }

    public List<CompanyCategoryResponseDTO> getCompanyCategories(Long countryId) {
        return companyCategoryGraphRepository.findCompanyCategoriesByCountry(countryId);
    }

    public CompanyCategoryResponseDTO updateCompanyCategory(Long countryId, CompanyCategoryDTO companyCategoryDTO) {

        if (companyCategoryDTO.getName().trim().isEmpty()) {
            throw new DataNotMatchedException("Name can't be blank");
        }
        CompanyCategory companyCategory = companyCategoryGraphRepository.findByCountryAndCompanycategory(countryId, companyCategoryDTO.getId());
        if (!Optional.ofNullable(companyCategory).isPresent()) {
            throw new DataNotFoundByIdException("Invalid CompanyCategory " + companyCategoryDTO.getId());
        }
        if (!companyCategoryDTO.getName().trim().equalsIgnoreCase(companyCategory.getName())) {
            boolean isAlreadyExists = companyCategoryGraphRepository.findByNameExcludingCurrent(countryId, companyCategoryDTO.getId(), "(?i)" + companyCategoryDTO.getName().trim());
            if (isAlreadyExists) {
                throw new DuplicateDataException("CompanyCategory already exists: " + companyCategoryDTO.getName().trim());
            }
        }
        companyCategory.setName(companyCategoryDTO.getName().trim());
        companyCategory.setDescription(companyCategoryDTO.getDescription());
        save(companyCategory);
        return new CompanyCategoryResponseDTO(companyCategory.getId(), companyCategory.getName(), companyCategory.getDescription());
    }

    public boolean deleteCompanyCategory(Long countryId, Long companyCategoryId) {
        CompanyCategory companyCategory = companyCategoryGraphRepository.findByCountryAndCompanycategory(countryId, companyCategoryId);
        if (!Optional.ofNullable(companyCategory).isPresent()) {
            throw new DataNotFoundByIdException("Invalid CompanyCategory " + companyCategoryId);
        }
        companyCategory.setDeleted(true);
        save(companyCategory);
        return true;
    }

}

package com.kairos.service.country;

import com.kairos.persistence.model.country.CompanyCategory;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.dto.CompanyCategoryResponseDTO;
import com.kairos.persistence.repository.user.country.CompanyCategoryGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.model.organization.company_category.CompanyCategoryDTO;
import com.kairos.service.UserBaseService;
import com.kairos.service.exception.ExceptionService;
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
    @Inject
    private ExceptionService exceptionService;


    public CompanyCategoryDTO createCompanyCategory(Long countryId, CompanyCategoryDTO companyCategoryDTO) {
        if (companyCategoryDTO.getName().trim().isEmpty()) {
            exceptionService.actionNotPermittedException("error.companyCategory.name.notEmpty");

        }
        Optional<Country> country = countryGraphRepository.findById(countryId);
        if (!Optional.ofNullable(country.get()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }
        boolean isAlreadyExists = companyCategoryGraphRepository.findByCountryAndNameExcludingCurrent(countryId, -1L, "(?i)" + companyCategoryDTO.getName().trim());
        if (isAlreadyExists) {
            exceptionService.duplicateDataException("message.companyCategory.name.alreadyExist", companyCategoryDTO.getName().trim());

        }
        CompanyCategory companyCategory = new CompanyCategory(companyCategoryDTO.getName().trim(), companyCategoryDTO.getDescription(), country.get());
        save(companyCategory);
        return new CompanyCategoryDTO(companyCategory.getId(), companyCategory.getName(), companyCategory.getDescription());
    }

    public List<CompanyCategoryResponseDTO> getCompanyCategories(Long countryId) {
        return companyCategoryGraphRepository.findCompanyCategoriesByCountry(countryId);
    }

    public CompanyCategoryResponseDTO updateCompanyCategory(Long countryId, CompanyCategoryDTO companyCategoryDTO) {

        if (companyCategoryDTO.getName().trim().isEmpty()) {
            exceptionService.actionNotPermittedException("error.companyCategory.name.notEmpty");

        }
        CompanyCategory companyCategory = companyCategoryGraphRepository.findByCountryAndCompanycategory(countryId, companyCategoryDTO.getId());
        if (!Optional.ofNullable(companyCategory).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.companyCategory.id.notFound",companyCategoryDTO.getId());

        }
        if (!companyCategoryDTO.getName().trim().equalsIgnoreCase(companyCategory.getName())) {
            boolean isAlreadyExists = companyCategoryGraphRepository.findByCountryAndNameExcludingCurrent(countryId, companyCategoryDTO.getId(), "(?i)" + companyCategoryDTO.getName().trim());
            if (isAlreadyExists) {
                exceptionService.duplicateDataException("message.companyCategory.name.alreadyExist", companyCategoryDTO.getName().trim());

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
            exceptionService.dataNotFoundByIdException("message.companyCategory.id.notFound",companyCategoryId);

        }
        companyCategory.setDeleted(true);
        save(companyCategory);
        return true;
    }

}

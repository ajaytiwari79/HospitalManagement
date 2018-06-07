package com.kairos.service.agreement_template;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.agreement_template.AgreementSection;
import com.kairos.persistance.repository.agreement_template.AgreementSectionMongoRepository;
import com.kairos.persistance.repository.clause.ClauseMongoRepository;
import com.kairos.response.dto.agreement_template.AgreementSectionResponseDto;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Transactional
@Service
public class AgreementSectionService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgreementSectionService.class);

    @Inject
    private AgreementSectionMongoRepository agreementSectionMongoRepository;

    @Inject
    private ClauseMongoRepository clauseMongoRepository;


    @Inject
    private ExceptionService exceptionService;


    public Map<String, Object> createAgreementSections(List<AgreementSection> agreementSections) {

        List<AgreementSectionResponseDto> result = new ArrayList<>();
        Set<BigInteger> ids = new HashSet<>();
        Map<String, Object> response = new HashMap<>();

        if (agreementSections.size() != 0) {

            for (AgreementSection agreementSection : agreementSections) {
                AgreementSection section = buildAgreementSection(UserContext.getCountryId(), agreementSection);
                ids.add(section.getId());

            }
            response.put("section", agreementSectionMongoRepository.getAgreementSectionWithDataList(UserContext.getCountryId(),ids));
            response.put("ids", ids);
            return response;
        } else
            throw new InvalidRequestException("agreement section list is empty");

    }

    public AgreementSection buildAgreementSection(Long countryId, AgreementSection agreementSection) {
        return save(new AgreementSection(countryId, agreementSection.getTitle(), agreementSection.getClauseIds()));

    }


    public Boolean deleteAgreementSection(BigInteger id) {

        AgreementSection exist = agreementSectionMongoRepository.findByid(id);
        if (Optional.ofNullable(exist).isPresent()) {
            exist.setDeleted(true);
            save(exist);
            return true;
        }
        throw new DataNotFoundByIdException(" agreement section for id " + id + " not exist");

    }


    public AgreementSectionResponseDto getAgreementSectionWithDataById(Long countryId, BigInteger id) {

        AgreementSectionResponseDto exist = agreementSectionMongoRepository.getAgreementSectionWithDataById(id);
        if (Optional.ofNullable(exist).isPresent()) {
            return exist;
        }
        throw new DataNotFoundByIdException("agreement section for id " + id + " not exist");

    }


    public List<AgreementSectionResponseDto> getAllAgreementSection(Long countryId) {

        List<AgreementSectionResponseDto> result = agreementSectionMongoRepository.getAllAgreementSectionWithData(countryId);
        if (result.size() != 0) {
            return result;
        }
        throw new DataNotExists("agreement section not exist create new sections");

    }


    public List<AgreementSectionResponseDto> getAgreementSectionWithDataList(Long countryId,Set<BigInteger> ids) {
        return agreementSectionMongoRepository.getAgreementSectionWithDataList(countryId,ids);

    }


}

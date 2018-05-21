package com.kairos.service.agreement_template;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.persistance.model.agreement_template.AgreementSection;
import com.kairos.persistance.repository.agreement_template.AgreementSectionMongoRepository;
import com.kairos.persistance.repository.clause.ClauseMongoRepository;
import com.kairos.response.dto.agreement_template.AgreementSectionResponseDto;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AgreementSectionService extends MongoBaseService {


    @Inject
    private AgreementSectionMongoRepository agreementSectionMongoRepository;

    @Inject
    private ClauseMongoRepository clauseMongoRepository;


    public AgreementSection createAgreementSection(AgreementSection agreementSection) {

        if (agreementSectionMongoRepository.findByTitle(agreementSection.getTitle()) != null) {
            throw new DuplicateDataException("section with name " + agreementSection.getTitle() + "  already exist");
        }
        for (BigInteger clauseId : agreementSection.getClauseIds()) {
            if (clauseMongoRepository.findByIdAndNonDeleted(clauseId) == null) {
                throw new DataNotFoundByIdException("clause for id  " + clauseId + "  not found");
            }
        }
        return save(new AgreementSection(agreementSection.getTitle(), agreementSection.getClauseIds()));

    }

    public Boolean deleteAgreementSection(BigInteger id) {

        AgreementSection exist = agreementSectionMongoRepository.findByIdAndNonDeleted(id);
        if (Optional.ofNullable(exist).isPresent()) {
            exist.setDeleted(true);
            save(exist);
            return true;
        }
        throw new DataNotFoundByIdException(" agreement section for id " + id + " not exist");

    }


    public AgreementSectionResponseDto getAgreementSectionWithDataById(BigInteger id) {

        AgreementSectionResponseDto exist = agreementSectionMongoRepository.getAgreementSectionWithDataById(id);
        if (Optional.ofNullable(exist).isPresent()) {
            return exist;
        }
        throw new DataNotFoundByIdException("agreement section for id " + id + " not exist");

    }


    public List<AgreementSectionResponseDto> getAllAgreementSection() {

        List<AgreementSectionResponseDto> result = agreementSectionMongoRepository.getAllAgreementSectionWithData();
        if (result.size() != 0) {
            return result;
        }
        throw new DataNotExists("agreement section not exist create new sections");

    }


    public List<AgreementSectionResponseDto> getAgreementSectionWithDataList(Set<BigInteger> ids) {

        return agreementSectionMongoRepository.getAgreementSectionWithDataList(ids);

    }


}

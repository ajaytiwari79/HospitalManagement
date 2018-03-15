package com.kairos.service.pay_table;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaQueryResult;
import com.kairos.persistence.model.user.pay_table.*;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayTableGraphRepository;
import com.kairos.response.dto.web.pay_table.PayTableDTO;
import com.kairos.response.dto.web.pay_table.PayTableResponseWrapper;
import com.kairos.service.UserBaseService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by prabjot on 26/12/17.
 */
@Service
public class PayTableService extends UserBaseService {

    @Inject
    private PayTableGraphRepository payTableGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;

    @Inject
    private PayGroupAreaGraphRepository payGroupAreaGraphRepository;

    private Logger logger = LoggerFactory.getLogger(PayTableService.class);

    private PayTable findById(Long payLevelId) {
        PayTable payTable = payTableGraphRepository.findOne(payLevelId);
        if (payTable == null) {
            throw new DataNotFoundByIdException("Invalid pay level id");
        }
        return payTable;
    }

    private PayLevelDTO getPayLevelResponse(PayTable payTable, PayLevelDTO payLevelDTO) {
        payLevelDTO.setId(payTable.getId());
        return payLevelDTO;
    }


    public PayTableResponseWrapper getPayTablesByOrganizationLevel(Long countryId, Long organizationLevelId) {

        Level level = countryGraphRepository.getLevel(countryId, organizationLevelId);
        if (!Optional.ofNullable(level).isPresent()) {
            throw new DataNotFoundByIdException("Invalid level in country");
        }
        List<PayGroupAreaQueryResult> payGroupAreas = payGroupAreaGraphRepository.getPayGroupAreaByOrganizationLevelId(organizationLevelId);
        List<PayTableQueryResult> payTables = payTableGraphRepository.findPayTableByOrganizationLevel(organizationLevelId);
        PayTableResponseWrapper responseWrapper = new PayTableResponseWrapper(payGroupAreas, payTables);
        return responseWrapper;

    }

    public PayTableQueryResult createPayLevel(Long countryId, PayTableDTO payTableDTO) {
        logger.info(payTableDTO.toString());
        Level level = countryGraphRepository.getLevel(countryId, payTableDTO.getLevelId());
        if (!Optional.ofNullable(level).isPresent()) {
            throw new DataNotFoundByIdException("Invalid level id " + payTableDTO.getLevelId());
        }

        Boolean isAlready = payTableGraphRepository.
                checkPayTableNameAlreadyExitsByNameOrShortName(countryId, -1L, "(?i)" + payTableDTO.getName().trim(),
                        "(?i)" + payTableDTO.getShortName().trim());
        if (isAlready) {
            throw new DuplicateDataException("Name " + payTableDTO.getName() + "or short Name " + payTableDTO.getShortName() + " already Exist in country " + countryId);
        }

        PayTable payTable = new PayTable(payTableDTO.getName().trim(), payTableDTO.getShortName().trim(), payTableDTO.getDescription(), level, payTableDTO.getStartDateMillis(), payTableDTO.getEndDateMillis());

        save(payTable);
        //  validatePayLevel(countryId, payLevelDTO);
        //  PayTable payTable = validatePayLevelMetaData(countryId, payLevelDTO);
        //save(payTable);

        PayTableQueryResult payTableQueryResult = new PayTableQueryResult(payTableDTO.getName().trim(), payTableDTO.getShortName().trim(), payTableDTO.getDescription(), payTableDTO.getStartDateMillis().getTime(),payTableDTO.getEndDateMillis().getTime());
        payTableQueryResult.setId(payTable.getId());
        payTableQueryResult.setLevel(level);
        return payTableQueryResult;
    }

    private void validatePayLevel(Long countryId, PayLevelDTO payLevelDTO) {

        List<PayLevelDTO> payLevels = payTableGraphRepository.findByOrganizationTypeAndExpertiseId(countryId,
                payLevelDTO.getOrganizationTypeId(), payLevelDTO.getExpertiseId(), payLevelDTO.getLevelId());
        payLevels.forEach(payLevelToValidate -> {

            if (payLevelToValidate.getEndDate() == null || payLevelDTO.getStartDate().compareTo(payLevelToValidate.getEndDate()) <= 0) {
                throw new DuplicateDataException("Pay level already exist");
            }
        });

    }

    private PayTable validatePayLevelMetaData(Long countryId, PayLevelDTO payLevelDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            throw new InternalError("Invalid countryId");
        }
        OrganizationType organizationType = organizationTypeGraphRepository.findOne(payLevelDTO.getOrganizationTypeId());
        if (organizationType == null) {
            throw new InternalError("Invalid Organization type id ");
        }
        Expertise expertise = expertiseGraphRepository.findOne(payLevelDTO.getExpertiseId());
        if (expertise == null) {
            throw new InternalError("Invalid expertise id ");
        }

        DateTime startDateAsJodaDate = new DateTime(payLevelDTO.getStartDate()).withHourOfDay(0).withMinuteOfHour(0).
                withSecondOfMinute(0).withMillisOfSecond(0);

        PayTable payTable = new PayTable();

        if (Optional.ofNullable(payLevelDTO.getLevelId()).isPresent()) {
            Level level = organizationTypeGraphRepository.getLevel(payLevelDTO.getOrganizationTypeId(), payLevelDTO.getLevelId());
            if (level == null) {
                throw new InternalError("Invalid level id");
            }
            payTable.setLevel(level);
        }

        if (payLevelDTO.getEndDate() != null) {
            DateTime endDateAsJodaDate = new DateTime(payLevelDTO.getEndDate()).withHourOfDay(0).withMinuteOfHour(0).
                    withSecondOfMinute(0).withMillisOfSecond(0);
            payTable.setEndDate(endDateAsJodaDate.toDate());
        }
        return payTable;
    }

    public PayTableDTO updatePayLevel(Long payLevelId, PayTableDTO payTableDTO) {
/*

        PayTable payTable = findById(payLevelId);
        DateTime startDateToUpdate = new DateTime(payLevelDTO.getStartDate()).withHourOfDay(0).withMinuteOfHour(0).
                withSecondOfMinute(0).withMillisOfSecond(0);
        DateTime payLevelDate = new DateTime(payTable.getStartDate()).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        DateTime currentDate = new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        if (startDateToUpdate.compareTo(payLevelDate) != 0 &&
                payLevelDate.compareTo(currentDate) < 0) {
            throw new InternalError("Start date can't be update");
        }
        DateTime startDateAsJodaDate = new DateTime(payLevelDTO.getStartDate()).withHourOfDay(0).withMinuteOfHour(0).
                withSecondOfMinute(0).withMillisOfSecond(0);
        payTable.setStartDate(startDateAsJodaDate.toDate());
        if (payLevelDTO.getEndDate() != null) {
            payTable.setEndDate(payLevelDTO.getEndDate());
        }
        payTable.setName(payLevelDTO.getName());
        save(payTable);
*/
        return payTableDTO;
    }
}

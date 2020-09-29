package com.kairos.service.pay_table;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.country.pay_table.PayTableDTO;
import com.kairos.dto.user.country.pay_table.PayTableUpdateDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.country.pay_table.PayGradeDTO;
import com.kairos.persistence.model.country.pay_table.PayGroupAreaDTO;
import com.kairos.persistence.model.country.pay_table.PayTableResponseWrapper;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.pay_table.*;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaQueryResult;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.functions.FunctionGraphRepository;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayGradeGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayTableGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayTableRelationShipGraphRepository;
import com.kairos.service.employment.EmploymentService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.expertise.FunctionalPaymentService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.constants.UserMessagesConstants.*;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by prabjot on 26/12/17.
 */
@Service
@Transactional
public class PayTableService {

    @Inject
    private PayTableGraphRepository payTableGraphRepository;
    @Inject
    private EmploymentService employmentService;
    @Inject
    private PayGradeGraphRepository payGradeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private PayGroupAreaGraphRepository payGroupAreaGraphRepository;
    @Inject
    private PayTableRelationShipGraphRepository payTableRelationShipGraphRepository;
    @Inject
    private FunctionGraphRepository functionGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private FunctionalPaymentService functionalPaymentService;
    private static final Logger LOGGER = LoggerFactory.getLogger(PayTableService.class);

    public PayTableResponseWrapper getPayTablesByOrganizationLevel(Long countryId, Long organizationLevelId, LocalDate startDate) {
        Level level = countryGraphRepository.getLevel(countryId, organizationLevelId);
        if (!Optional.ofNullable(level).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYTABLE_LEVEL_NOTFOUND);

        }
        List<PayGroupAreaQueryResult> payGroupAreaQueryResults = payGroupAreaGraphRepository.getPayGroupAreaByOrganizationLevelId(organizationLevelId);
        List<FunctionDTO> functions = functionGraphRepository.getFunctionsByOrganizationLevel(organizationLevelId);
        List<PayTableResponse> payTableQueryResults = payTableGraphRepository.findActivePayTablesByOrganizationLevel(organizationLevelId, startDate.toString());
        PayTableResponse payTable = null;
        if (payTableQueryResults.size() > 1) {
            // multiple payTables are found NOW need to filter by date
            payTable = getValidPayTable(startDate, payTableQueryResults, payTable);
        } else if (payTableQueryResults.size() == 1)
            payTable = payTableQueryResults.get(0);
        return new PayTableResponseWrapper(payGroupAreaQueryResults, payTable, functions);
    }

    private PayTableResponse getValidPayTable(LocalDate startDate, List<PayTableResponse> payTableQueryResults, PayTableResponse payTable) {
        for (PayTableResponse currentPayTable : payTableQueryResults) {
            if (Optional.ofNullable(currentPayTable.getEndDateMillis()).isPresent() &&
                    (currentPayTable.getEndDateMillis().isAfter(startDate) || currentPayTable.getEndDateMillis().isEqual(startDate))
                    && (currentPayTable.getStartDateMillis().isBefore(startDate) || currentPayTable.getStartDateMillis().isEqual(startDate))) {
                payTable = currentPayTable;
                break;
            }
            if (!Optional.ofNullable(currentPayTable.getEndDateMillis()).isPresent() &&
                    (currentPayTable.getStartDateMillis().isBefore(startDate) || currentPayTable.getStartDateMillis().isEqual(startDate))) {
                payTable = currentPayTable;
                break;
            }
        }
        return payTable;
    }

    public List<OrganizationLevelPayGroupAreaDTO> getOrganizationLevelWisePayGroupAreas(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_LEVEL_ID_NOTFOUND, countryId);
        }
        return payTableGraphRepository.getOrganizationLevelWisePayGroupAreas(countryId);
    }

    public PayTableResponse createPayTable(Long countryId, PayTableDTO payTableDTO) {
        LOGGER.info(payTableDTO.toString());
        Level level = countryGraphRepository.getLevel(countryId, payTableDTO.getLevelId());
        if (!Optional.ofNullable(level).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYTABLE_LEVEL_NOTFOUND);
        }

        Boolean isAlreadyExists = payTableGraphRepository.
                checkPayTableNameAlreadyExitsByName(countryId, -1L, "(?i)" + payTableDTO.getName().trim());
        if (isAlreadyExists) {
            exceptionService.duplicateDataException(MESSAGE_PAYTABLE_NAME_ALREADYEXIST, payTableDTO.getName(), countryId);
        }
        PayTable payTableToValidate = payTableGraphRepository.findPayTableByOrganizationLevel(payTableDTO.getLevelId(), -1L);
        // if any payTable is found then only validate
        if (Optional.ofNullable(payTableToValidate).isPresent()) {
            if (payTableToValidate.getEndDateMillis() == null) {
                payTableToValidate.setEndDateMillis(payTableDTO.getStartDateMillis().minusDays(1));
                payTableGraphRepository.save(payTableToValidate);
            } else {
                validatePayLevel(payTableToValidate, payTableDTO.getStartDateMillis(), payTableDTO.getEndDateMillis());
            }
        }

        PayTable payTable = new PayTable(payTableDTO.getName().trim(), payTableDTO.getShortName(), payTableDTO.getDescription(), level, payTableDTO.getStartDateMillis(), null, payTableDTO.getPaymentUnit(), true);
        payTableGraphRepository.save(payTable);
        PayTableResponse payTableResponse = new PayTableResponse(payTable.getName(), payTable.getShortName(), payTable.getDescription(), payTable.getStartDateMillis(), payTable.getEndDateMillis(), payTable.isPublished(), payTable.getPaymentUnit(), payTable.isEditable());
        payTableResponse.setId(payTable.getId());
        payTableResponse.setLevel(level);
        return payTableResponse;
    }


    private void validatePayLevel(PayTable payTableToValidate, LocalDate startDateMillis, LocalDate endDateMillis) {
        if (payTableToValidate.getEndDateMillis() != null) {
            LOGGER.info("new  startDate{}", startDateMillis + "  End date " + endDateMillis);
            long days = DAYS.between(startDateMillis, payTableToValidate.getEndDateMillis());
            LOGGER.info("difference in days" + days);
            if (days != -1) {
                exceptionService.actionNotPermittedException(MESSAGE_STARTDATE_ALLOWED, payTableToValidate.getEndDateMillis().plusDays(1));
            }
        } else {
            exceptionService.actionNotPermittedException(MESSAGE_PAYTABLE_ALREADYACTIVE, payTableToValidate.getName(), payTableToValidate.getId());
        }
    }

    private void prepareDates(PayTable payTable, PayTableUpdateDTO payTableDTO) {
        validateAndSetStartDateInPayTable(payTable, payTableDTO);
        // End date
        // If already end date was set  but now no value so we are removing
        if (!Optional.ofNullable(payTableDTO.getEndDateMillis()).isPresent() && Optional.ofNullable(payTable.getEndDateMillis()).isPresent()) {
            payTable.setEndDateMillis(null);

        }
        // If already not present now its present    Previous its absent
        else if (!Optional.ofNullable(payTable.getEndDateMillis()).isPresent() && Optional.ofNullable(payTableDTO.getEndDateMillis()).isPresent()) {
            if (payTableDTO.getEndDateMillis().isBefore(DateUtils.getCurrentLocalDate())) {
                exceptionService.actionNotPermittedException(MESSAGE_ENDTDATE_LESSTHAN);

            }
            payTable.setEndDateMillis(payTableDTO.getEndDateMillis());
        }
        // If already present and still present // NOw checking are they same or different
        else if (Optional.ofNullable(payTable.getEndDateMillis()).isPresent() && Optional.ofNullable(payTableDTO.getEndDateMillis()).isPresent()) {
            if (!payTable.getEndDateMillis().equals(payTableDTO.getEndDateMillis())) {//The end date is modified Now We need to compare is it less than today
                if (payTableDTO.getEndDateMillis().isBefore(DateUtils.getCurrentLocalDate())) {
                    exceptionService.actionNotPermittedException(MESSAGE_ENDTDATE_LESSTHAN);

                }
                payTable.setEndDateMillis(payTableDTO.getEndDateMillis());
            }
        }
    }

    private void validateAndSetStartDateInPayTable(PayTable payTable, PayTableUpdateDTO payTableDTO) {
        if (!payTable.getStartDateMillis().equals(payTableDTO.getStartDateMillis())) {
            // The start date is modified Now We need to compare is it less than today
            if (payTableDTO.getStartDateMillis().isBefore(DateUtils.getCurrentLocalDate())) {
                exceptionService.actionNotPermittedException(MESSAGE_STARTDATE_LESSTHAN);
            }
            payTable.setStartDateMillis(payTableDTO.getStartDateMillis());
        }
    }

    // update basic detail of pay Table we are not making any copy as this time  as this does not impact any calculation
    public PayTableResponse updatePayTable(Long countryId, Long payTableId, PayTableUpdateDTO payTableDTO) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYTABLE_ID_NOTFOUND);
        }
        // if  name or short name is changes then only we are checking its name existance
        // skipped to current pay table it is checking weather any other payTable has same name or short name in current organization level
        if (!payTableDTO.getName().trim().equalsIgnoreCase(payTable.getName())) {
            Boolean isAlreadyExists = payTableGraphRepository.
                    checkPayTableNameAlreadyExitsByName(countryId, payTableId, "(?i)" + payTableDTO.getName().trim());
            if (isAlreadyExists) {
                exceptionService.duplicateDataException(MESSAGE_PAYTABLE_NAME_ALREADYEXIST, payTableDTO.getName(), countryId);
            }
        }
        PayTable payTableToValidate = payTableGraphRepository.findPayTableByOrganizationLevel(payTableDTO.getLevelId(), payTableId);
        if (Optional.ofNullable(payTableToValidate).isPresent()) {
            validatePayLevel(payTableToValidate, payTableDTO.getStartDateMillis(), payTableDTO.getEndDateMillis());
        }
        payTable.setName(payTableDTO.getName().trim());
        payTable.setShortName(payTableDTO.getShortName());
        payTable.setDescription(payTableDTO.getDescription());
        payTable.setPaymentUnit(payTableDTO.getPaymentUnit());
        prepareDates(payTable, payTableDTO);
        payTableGraphRepository.save(payTable);
        PayTableResponse payTableResponse = new PayTableResponse(payTable.getName(), payTable.getShortName(), payTable.getDescription(), payTable.getStartDateMillis(), payTable.getEndDateMillis(), payTable.isPublished(), payTable.getPaymentUnit(), payTable.isEditable());
        payTableResponse.setId(payTable.getId());
        return payTableResponse;
    }

    public List<PayGradeResponse> addPayGradeInPayTable(Long payTableId, PayGradeDTO payGradeDTO) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYTABLE_ID_NOTFOUND);

        }
        Boolean isAlreadyExists = payTableGraphRepository.checkPayGradeLevelAlreadyExists(payTableId, payGradeDTO.getPayGradeLevel());
        if (isAlreadyExists) {
            exceptionService.duplicateDataException(MESSAGE_PAYGRADE_LEVEL_ALREADYEXIST, payGradeDTO.getPayGradeLevel());

        }
        // payTable is not published
        List<PayGradeResponse> payGradesData = new ArrayList<>();
        if (!payTable.isPublished()) {
            payGradesData.add(addPayGradeInCurrentPayTable(payTable, payGradeDTO));
        } else {
            payGradesData = createCopyOfPayTableAndAddPayGrade(payTable, payGradeDTO, null);
        }
        return payGradesData;
    }

    private List<PayGradeResponse> createCopyOfPayTableAndAddPayGrade(PayTable payTable, PayGradeDTO payGradeDTO, List<PayGradePayGroupAreaRelationShip> payGradesPayGroupAreaRelationShips) {
        List<PayGradeResponse> payGradeResponses = new ArrayList<>();
        PayTable copiedPayTable = initializeCopiedPayTable(payTable);
        if (payGradeDTO != null) {
            payGradeResponses.add(addPayGradeInCurrentPayTable(copiedPayTable, payGradeDTO));
        }
        copiedPayTable.setPublished(false);
        payTableGraphRepository.save(copiedPayTable);
        //copying all previous and then adding in pay Table as well.
        List<PayGrade> payGradesObjects = new ArrayList<>();
        if (CollectionUtils.isEmpty(payGradesPayGroupAreaRelationShips)) {
            createPayGradePayGroupAreaRelationShip(payTable, payGradeResponses, copiedPayTable, payGradesObjects);
        } else {
            Set<PayGrade> payGrades = payGradesPayGroupAreaRelationShips.stream().map(PayGradePayGroupAreaRelationShip::getPayGrade).collect(Collectors.toSet());
            payGradeGraphRepository.saveAll(payGrades);
            Map<Long, PayGrade> longPayGradeMap = payGrades.stream().collect(Collectors.toMap(PayGrade::getPayGradeLevel, Function.identity(), (previous, current) -> current));
            payGradesPayGroupAreaRelationShips.forEach(payGradesRelationShips -> payGradesRelationShips.setPayGrade(longPayGradeMap.get(payGradesRelationShips.getPayGrade().getPayGradeLevel())));
            payTableRelationShipGraphRepository.saveAll(payGradesPayGroupAreaRelationShips);
            payGradesObjects.addAll(payGrades);
        }
        // Adding new Grade in PayTable
        copiedPayTable.getPayGrades().addAll(payGradesObjects);
        payTableGraphRepository.save(copiedPayTable);
        if (payGradeResponses.isEmpty()) {
            payGradeResponses.add(new PayGradeResponse(copiedPayTable.getId()));
        }
        return payGradeResponses;
    }

    private PayTable initializeCopiedPayTable(PayTable payTable) {
        PayTable copiedPayTable = new PayTable();
        BeanUtils.copyProperties(payTable, copiedPayTable);
        copiedPayTable.setId(null);
        copiedPayTable.setPayTable(payTable);
        copiedPayTable.setPayGrades(null);
        copiedPayTable.setPercentageValue(payTable.getPercentageValue());
        payTable.setPercentageValue(null);
        return copiedPayTable;
    }

    private void createPayGradePayGroupAreaRelationShip(PayTable payTable, List<PayGradeResponse> payGradeResponses, PayTable copiedPayTable, List<PayGrade> payGradesObjects) {
        for (PayGrade currentPayGrade : payTable.getPayGrades()) {
            PayGrade newPayGrade = new PayGrade(currentPayGrade.getPayGradeLevel(), false);
            List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips = new ArrayList<>();
            HashSet<PayTableMatrixQueryResult> payTableMatrix = payGradeGraphRepository.getPayGradeMatrixByPayGradeId(currentPayGrade.getId());
            payTableMatrix.forEach(currentObj -> {
                PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip = new PayGradePayGroupAreaRelationShip(newPayGrade, new PayGroupArea(currentObj.getPayGroupAreaId(), currentObj.getPayGroupAreaName()), currentObj.getPayGroupAreaAmount());
                payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);
            });
            payTableRelationShipGraphRepository.saveAll(payGradePayGroupAreaRelationShips);
            payGradesObjects.add(newPayGrade);
            PayGradeResponse payGradeResponse =
                    new PayGradeResponse(copiedPayTable.getId(), newPayGrade.getPayGradeLevel(), newPayGrade.getId(), getPayGradeResponse(payGradePayGroupAreaRelationShips), newPayGrade.isPublished());
            payGradeResponses.add(payGradeResponse);
        }
    }

    private PayGradeResponse addPayGradeInCurrentPayTable(PayTable payTable, PayGradeDTO payGradeDTO) {
        Set<Long> payGroupAreasId = payGradeDTO.getPayGroupAreas().stream().map(PayGroupAreaDTO::getPayGroupAreaId).collect(Collectors.toSet());
        List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllByIds(payGroupAreasId);
        if (payGroupAreas.size() != payGroupAreasId.size()) {
            exceptionService.dataNotMatchedException(MESSAGE_PAYGROUPAREA_UNABLETOGET);

        }
        PayGrade payGrade = new PayGrade(payGradeDTO.getPayGradeLevel());
        payTable.getPayGrades().add(payGrade);

        payTableGraphRepository.save(payTable);
        List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips = new ArrayList<>();
        // contains all
        payGroupAreas.forEach(currentPayGroupArea -> {
            for (int i = 0; i < payGradeDTO.getPayGroupAreas().size(); i++) {
                if (payGradeDTO.getPayGroupAreas().get(i).getPayGroupAreaId().equals(currentPayGroupArea.getId())) {
                    PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip
                            = new PayGradePayGroupAreaRelationShip(payGrade, currentPayGroupArea, payGradeDTO.getPayGroupAreas().get(i).getPayGroupAreaAmount());
                    payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);
                }
            }
        });
        payTableRelationShipGraphRepository.saveAll(payGradePayGroupAreaRelationShips);
        return new PayGradeResponse(payTable.getId(), payGrade.getPayGradeLevel(), payGrade.getId(), getPayGradeResponse(payGradePayGroupAreaRelationShips), payGrade.isPublished());
    }

    private List<PayGroupAreaDTO> getPayGradeResponse(List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips) {
        List<PayGroupAreaDTO> payGradeMatrices = new ArrayList<>();
        payGradePayGroupAreaRelationShips.forEach(currentPayGroupArea -> {
            PayGroupAreaDTO payGroupAreaDTO =
                    new PayGroupAreaDTO(currentPayGroupArea.getPayGroupArea().getId(), currentPayGroupArea.getPayGroupAreaAmount(), currentPayGroupArea.getId());
            payGradeMatrices.add(payGroupAreaDTO);
        });
        return payGradeMatrices;
    }

    public List<PayGradeResponse> getPayGradesByPayTableId(Long payTableId) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);

        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYTABLE_ID_NOTFOUND);

        }
        return payTableGraphRepository.getPayGradesByPayTableId(payTableId);
    }

    public boolean removePayGradeInPayTable(Long payTableId, Long payGradeId) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYTABLE_ID_NOTFOUND);

        }
        if (payTable.isPublished()) {
            exceptionService.actionNotPermittedException(MESSAGE_PAYTABLE_ALREADYPUBLISHED);

        }
        if (!Optional.ofNullable(payTable.getPayGrades()).isPresent() || payTable.getPayGrades().isEmpty()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYTABLE_ID_NOTFOUND);

        }
        boolean found = false;
        for (PayGrade currentPayGrade : payTable.getPayGrades()) {
            if (currentPayGrade.getId().equals(payGradeId)) {
                found = true;
                break;
            }
        }
        if (found)
            payGradeGraphRepository.removeAllPayGroupAreasFromPayGradeAndDeletePayGrade(payGradeId);
        return found;
    }

    public PayTableResponse removePayTable(Long payTableId) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYTABLE_ID_NOTFOUND);

        }
        if (payTable.isPublished()) {
            exceptionService.actionNotPermittedException(MESSAGE_PAYTABLE_ALREADYPUBLISHED);

        }
        PayTableResponse parentPayTable = payTableGraphRepository.getParentPayTableByPayTableId(payTableId);
        payTable.setPayTable(null);
        payTable.setDeleted(true);
        payTableGraphRepository.save(payTable);

        return parentPayTable;
    }

    private List<PayGradeResponse> updatePayGradeInUnpublishedPayTable(PayTable payTable, PayGradeDTO payGradeDTO, PayGrade payGrade) {
        List<PayGradeResponse> payGradeResponses = new ArrayList<>();
        Set<Long> payGroupAreaIds = payGradeDTO.getPayGroupAreas().stream().map(PayGroupAreaDTO::getPayGroupAreaId).collect(Collectors.toSet());
        List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllByIds(payGroupAreaIds);
        List<PayGroupAreaDTO> payGroupAreaDTOS = payGradeGraphRepository.getPayGradeDataByIdAndPayGroupArea(payGrade.getId(), new ArrayList<>(payGroupAreaIds));
        Map<Long, BigDecimal> payGradePublishedAmountMap = payGroupAreaDTOS.stream().collect(Collectors.toMap(PayGroupAreaDTO::getPayGroupAreaId, PayGroupAreaDTO::getPublishedAmount));
        // removing all previous Ids
        payGradeGraphRepository.removeAllPayGroupAreasFromPayGrade(payGrade.getId());
        List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips = new ArrayList<>();
        for (PayGroupAreaDTO currentPayGroup : payGradeDTO.getPayGroupAreas()) {
            PayGroupArea currentPayGroupArea = payGroupAreas.stream().filter(payGroupArea -> payGroupArea.getId().equals(currentPayGroup.getPayGroupAreaId())).findFirst().get();
            PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip
                    = new PayGradePayGroupAreaRelationShip(payGrade, currentPayGroupArea, currentPayGroup.getPayGroupAreaAmount());
            payTableRelationShipGraphRepository.save(payGradePayGroupAreaRelationShip);
            payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);
            currentPayGroup.setId(payGradePayGroupAreaRelationShip.getId());
        }
        PayGradeResponse payGradeResponse =
                new PayGradeResponse(payTable.getId(), payGrade.getPayGradeLevel(), payGrade.getId(), getPayGradeResponse(payGradePayGroupAreaRelationShips), payGrade.isPublished());

        payGradeResponse.getPayGroupAreas().forEach(current -> current.setPublishedAmount(payGradePublishedAmountMap.get(current.getPayGroupAreaId())));
        payGradeResponses.add(payGradeResponse);
        return payGradeResponses;
    }

    public List<PayGradeResponse> updatePayGradeInPayTable(Long payTableId, Long payGradeId, PayGradeDTO payGradeDTO) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYTABLE_ID_NOTFOUND);

        }
        PayGrade payGrade = null;
        for (PayGrade currentPayGrade : payTable.getPayGrades()) {
            if (currentPayGrade.getId().equals(payGradeId)) {
                payGrade = currentPayGrade;
                break;
            }
        }
        if (!Optional.ofNullable(payGrade).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYGRADE_ID_NOTFOUND, payGradeId);

        }
        List<PayGradeResponse> payGradeResponses;
        //user is updating in a unpublished payTable
        payGradeResponses = (!payTable.isPublished()) ? updatePayGradeInUnpublishedPayTable(payTable, payGradeDTO, payGrade) :
                updatePayGradeInPublishedPayTable(payTable, payGradeDTO, payGradeId);
        return payGradeResponses;
    }

    private List<PayGradeResponse> updatePayGradeInPublishedPayTable(PayTable payTable, PayGradeDTO payGradeDTO, Long payGradeId) {
        List<PayGradeResponse> payGradeResponses = new ArrayList<>();
        List<PayGrade> payGradesObjects = new ArrayList<>();
        // creating a new PayTable
        PayTable payTableByMapper = preparePayTableByMapper(payTable);
        Set<Long> payGroupAreaIds = payGradeDTO.getPayGroupAreas().stream().map(PayGroupAreaDTO::getPayGroupAreaId).collect(Collectors.toSet());
        List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllByIds(payGroupAreaIds);
        List<PayGroupAreaDTO> payGroupAreaDTOS = payGradeGraphRepository.getPayGradeDataByIdAndPayGroupArea(payGradeId, new ArrayList<>(payGroupAreaIds));
        Map<Long, BigDecimal> payGradePublishedAmountMap = getMapOfPayGroupAreaAmount(payGroupAreaDTOS);
        for (PayGrade currentPayGrade : payTable.getPayGrades()) {
            PayGrade newPayGrade = new PayGrade(currentPayGrade.getPayGradeLevel(), false);
            List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips = getPayGradePayGroupAreaRelationShips(payGradeDTO, payGroupAreas, currentPayGrade, newPayGrade);
            payTableRelationShipGraphRepository.saveAll(payGradePayGroupAreaRelationShips);
            payGradesObjects.add(newPayGrade);
            PayGradeResponse payGradeResponse = new PayGradeResponse(payTableByMapper.getId(), newPayGrade.getPayGradeLevel(), newPayGrade.getId(), getPayGradeResponse(payGradePayGroupAreaRelationShips), newPayGrade.isPublished());
            if (currentPayGrade.getId().equals(payGradeId)) {
                payGradeResponse.getPayGroupAreas().forEach(current -> current.setPublishedAmount(payGradePublishedAmountMap.get(current.getPayGroupAreaId())));
            }
            payGradeResponses.add(payGradeResponse);
        }
        payTableByMapper.setPayGrades(payGradesObjects);
        payTableGraphRepository.save(payTableByMapper);
        return payGradeResponses;
    }

    private List<PayGradePayGroupAreaRelationShip> getPayGradePayGroupAreaRelationShips(PayGradeDTO payGradeDTO, List<PayGroupArea> payGroupAreas, PayGrade currentPayGrade, PayGrade newPayGrade) {
        List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips = new ArrayList<>();

        if (payGradeDTO.getPayGradeId().equals(currentPayGrade.getId())) {
            // user has changed the value in  this pay Grade area of payTable
            for (PayGroupAreaDTO currentPayGroupArea : payGradeDTO.getPayGroupAreas()) {
                PayGroupArea payGroupArea = payGroupAreas.stream().filter(payGroupArea1 -> payGroupArea1.getId().equals(currentPayGroupArea.getPayGroupAreaId())).findFirst().get();
                PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip = new PayGradePayGroupAreaRelationShip(newPayGrade, payGroupArea, currentPayGroupArea.getPayGroupAreaAmount());
                payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);
            }
        } else {
            HashSet<PayTableMatrixQueryResult> payTableMatrix = payGradeGraphRepository.getPayGradeMatrixByPayGradeId(currentPayGrade.getId());
            payTableMatrix.forEach(currentObj -> {
                PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip = new PayGradePayGroupAreaRelationShip(newPayGrade, new PayGroupArea(currentObj.getPayGroupAreaId(), currentObj.getPayGroupAreaName()), currentObj.getPayGroupAreaAmount());
                payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);
            });
        }
        return payGradePayGroupAreaRelationShips;
    }

    private PayTable preparePayTableByMapper(PayTable payTable) {
        PayTable payTableByMapper = new PayTable();
        BeanUtils.copyProperties(payTable, payTableByMapper);
        payTableByMapper.setId(null);
        payTableByMapper.setPayTable(payTable);
        payTableByMapper.setPayGrades(null);
        payTableByMapper.setPublished(false);
        payTable.setEditable(false);
        payTableGraphRepository.save(payTableByMapper);
        return payTableByMapper;
    }

    public List<PayTable> publishPayTable(Long payTableId, LocalDate publishedDate) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        validateDetails(payTable);
        List<PayTable> response = new ArrayList<>();
        PayTable parentPayTable = payTableGraphRepository.getPermanentPayTableByPayTableId(payTableId);
        if (Optional.ofNullable(parentPayTable).isPresent()) {
            LocalDate endDate = getEndDateToSet(publishedDate, parentPayTable);
            payTableGraphRepository.changeStateOfRelationShip(parentPayTable.getId(), endDate.toString());
            validatePayTableToPublish(payTableId, publishedDate);
            parentPayTable.setEndDateMillis(endDate);
            parentPayTable.setPayTable(null);
            response.add(parentPayTable);
        } else if (!payTable.getStartDateMillis().equals(publishedDate)) {
            exceptionService.actionNotPermittedException(MESSAGE_PAYTABLE_PUBLISHED_SAMEDATE);
        }
        payTable.setPayTable(null);
        payTable.setPublished(true);
        payTable.setStartDateMillis(publishedDate);
        payTable.getPayGrades().forEach(currentPayGrade -> currentPayGrade.setPublished(true));
        payTableGraphRepository.save(payTable);
        if (parentPayTable != null) {
            employmentService.createEmploymentLineOnPayTableChanges(payTable);
            if (payTable.getPercentageValue() != null) {
                functionalPaymentService.updateAmountInFunctionalTable(parentPayTable.getId(), payTable.getStartDateMillis(), payTable.getEndDateMillis(), payTable.getPercentageValue());
            }
        }
        response.add(payTable);
        return response;
    }

    private LocalDate getEndDateToSet(LocalDate publishedDate, PayTable parentPayTable) {
        LocalDate endDate;
        if (DateUtils.getLocalDate().equals(publishedDate) || parentPayTable.getStartDateMillis().equals(publishedDate)) {
            endDate = publishedDate;
        } else {
            endDate = publishedDate.minusDays(1);
        }
        return endDate;
    }

    private void validateDetails(PayTable payTable) {
        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYTABLE_ID_NOTFOUND);
        }
        if (payTable.isPublished()) {
            exceptionService.actionNotPermittedException(MESSAGE_PAYTABLE_PUBLISHED);

        }
        if (CollectionUtils.isEmpty(payTable.getPayGrades())) {
            exceptionService.actionNotPermittedException(MESSAGE_PAYGRADE_ABSENT);
        }
    }

    public List<PayTableResponse> getPayTablesByOrganizationLevel(Long organizationLevelId) {
        return payTableGraphRepository.findActivePayTablesByOrganizationLevel(organizationLevelId);
    }

    public PayTableUpdateDTO updatePayTableAmountByPercentage(Long payTableId, PayTableDTO payTableDTO) {
        if (payTableDTO.getPercentageValue() == null || payTableDTO.getPercentageValue().equals(new BigDecimal(0))) {
            exceptionService.actionNotPermittedException(EXCEPTION_NULL_PERCENTAGEVALUE);
        }
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        PayTable parentPayTable = null;
        Map<String, PayGradePayGroupAreaRelationShip> publishedPayGroupAreaRelationShipMap = null;
        if (!payTable.isPublished() && payTable.getPayTable() != null) {
            parentPayTable = payTable.getPayTable();
            List<PayGradePayGroupAreaRelationShip> publishedPayGradePayGroupAreaRelationShips = ObjectMapperUtils.copyCollectionPropertiesByMapper(payTableRelationShipGraphRepository.findAllByPayTableId(parentPayTable.getId()), PayGradePayGroupAreaRelationShip.class);
            publishedPayGroupAreaRelationShipMap = publishedPayGradePayGroupAreaRelationShips.stream().collect(Collectors.toMap(k -> k.getPayGrade().getPayGradeLevel().toString() + k.getPayGroupArea().getId(), v -> v));
        }

        List<PayGradeResponse> payGradeResponses = null;
        payTable.setPercentageValue(payTableDTO.getPercentageValue());
        List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips = ObjectMapperUtils.copyCollectionPropertiesByMapper(payTableRelationShipGraphRepository.findAllByPayTableId(payTableId), PayGradePayGroupAreaRelationShip.class);
        if (CollectionUtils.isNotEmpty(payGradePayGroupAreaRelationShips)) {
            updateAmountInRelationship(payTableDTO, payTable, parentPayTable, publishedPayGroupAreaRelationShipMap, payGradePayGroupAreaRelationShips);
            if (payTable.isPublished()) {
                payGradeResponses = createCopyOfPayTableAndAddPayGrade(payTable, null, payGradePayGroupAreaRelationShips);
            } else {
                payTableRelationShipGraphRepository.saveAll(payGradePayGroupAreaRelationShips);
                payTableGraphRepository.save(payTable);
            }
        }
        Long id = CollectionUtils.isEmpty(payGradeResponses) ? payTable.getId() : payGradeResponses.get(0).getPayTableId();
        return new PayTableUpdateDTO(id, payTable.getName(), payTable.getPercentageValue());
    }

    private void updateAmountInRelationship(PayTableDTO payTableDTO, PayTable payTable, PayTable parentPayTable, Map<String, PayGradePayGroupAreaRelationShip> publishedPayGroupAreaRelationShipMap, List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips) {
        for (PayGradePayGroupAreaRelationShip current : payGradePayGroupAreaRelationShips) {
            if (current.getPayGroupAreaAmount() != null) {
                BigDecimal valueToAdd = (parentPayTable == null) ? current.getPayGroupAreaAmount().multiply(payTableDTO.getPercentageValue()).divide(new BigDecimal(100)) : publishedPayGroupAreaRelationShipMap.get(current.getPayGrade().getPayGradeLevel().toString() + current.getPayGroupArea().getId()).getPayGroupAreaAmount().multiply(payTableDTO.getPercentageValue()).divide(new BigDecimal(100));
                BigDecimal updatedValue = (parentPayTable == null) ? current.getPayGroupAreaAmount().add(valueToAdd) : publishedPayGroupAreaRelationShipMap.get(current.getPayGrade().getPayGradeLevel().toString() + current.getPayGroupArea().getId()).getPayGroupAreaAmount().add(valueToAdd);
                current.setPayGroupAreaAmount(updatedValue);
                if (payTable.isPublished()) {
                    current.setId(null);
                    current.getPayGrade().setId(null);
                }
            }
        }
    }

    private void validatePayTableToPublish(Long payTableId, LocalDate publishedDate) {
        if (payTableGraphRepository.existsByDate(payTableId, publishedDate.toString())) {
            exceptionService.actionNotPermittedException(PUBLISHED_PAY_TABLE_EXISTS, publishedDate);
        }
    }

    private Map<Long, BigDecimal>  getMapOfPayGroupAreaAmount(List<PayGroupAreaDTO> payGroupAreaDTOS){
        Map<Long, BigDecimal> payGradePublishedAmountMap=new HashMap<>();
        for (PayGroupAreaDTO payGroupAreaDTO : payGroupAreaDTOS){
            payGradePublishedAmountMap.put(payGroupAreaDTO.getPayGroupAreaId(),payGroupAreaDTO.getPublishedAmount());
        }
        return payGradePublishedAmountMap;
    }

    public Map<String, TranslationInfo> updateTranslation(Long paytableId, Map<String,TranslationInfo> translations) {
        Map<String,String> translatedNames = new HashMap<>();
        Map<String,String> translatedDescriptios = new HashMap<>();
        for(Map.Entry<String,TranslationInfo> entry :translations.entrySet()){
            translatedNames.put(entry.getKey(),entry.getValue().getName());
            translatedDescriptios.put(entry.getKey(),entry.getValue().getDescription());
        }
        PayTable payTable =payTableGraphRepository.findOne(paytableId);
        payTable.setTranslatedNames(translatedNames);
        payTable.setTranslatedDescriptions(translatedDescriptios);
        payTableGraphRepository.save(payTable);
        return payTable.getTranslatedData();
    }
}

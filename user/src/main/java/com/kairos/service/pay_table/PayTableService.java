package com.kairos.service.pay_table;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.country.pay_table.PayGradeDTO;
import com.kairos.persistence.model.country.pay_table.PayGroupAreaDTO;
import com.kairos.persistence.model.country.pay_table.PayTableResponseWrapper;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.pay_table.*;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaQueryResult;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.functions.FunctionGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayGradeGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayTableGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayTableRelationShipGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.country.pay_table.PayTableDTO;
import com.kairos.dto.user.country.pay_table.PayTableUpdateDTO;
import com.kairos.service.expertise.FunctionalPaymentService;
import com.kairos.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static javax.management.timer.Timer.ONE_DAY;

/**
 * Created by prabjot on 26/12/17.
 */
@Service
@Transactional
public class PayTableService {

    @Inject
    private PayTableGraphRepository payTableGraphRepository;
    @Inject
    private PayGradeGraphRepository payGradeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;

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
    private final Logger logger = LoggerFactory.getLogger(PayTableService.class);


    public PayTableResponseWrapper getPayTablesByOrganizationLevel(Long countryId, Long organizationLevelId, Long startDate) {
        Level level = countryGraphRepository.getLevel(countryId, organizationLevelId);
        if (!Optional.ofNullable(level).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.paytable.level.notfound");

        }

        List<PayGroupAreaQueryResult> payGroupAreaQueryResults = payGroupAreaGraphRepository.getPayGroupAreaByOrganizationLevelId(organizationLevelId);
        List<FunctionDTO> functions = functionGraphRepository.getFunctionsByOrganizationLevel(organizationLevelId);
        List<PayTableResponse> payTableQueryResults = payTableGraphRepository.findActivePayTablesByOrganizationLevel(organizationLevelId, startDate);
        PayTableResponse payTable = null;
        if (payTableQueryResults.size() > 1) {
            // multiple payTables are found NOW need to filter by date
            for (PayTableResponse currentPayTable : payTableQueryResults) {
                if (Optional.ofNullable(currentPayTable.getEndDateMillis()).isPresent() &&
                        (new DateTime(currentPayTable.getEndDateMillis()).isAfter(new DateTime(startDate)) || new DateTime(currentPayTable.getEndDateMillis()).isEqual(new DateTime(startDate)))
                        && (new DateTime(currentPayTable.getStartDateMillis()).isBefore(new DateTime(startDate)) || new DateTime(currentPayTable.getStartDateMillis()).isEqual(new DateTime(startDate)))) {
                    payTable = currentPayTable;
                    break;
                }
                if (!Optional.ofNullable(currentPayTable.getEndDateMillis()).isPresent() &&
                        (new DateTime(currentPayTable.getStartDateMillis()).isBefore(new DateTime(startDate)) || new DateTime(currentPayTable.getStartDateMillis()).isEqual(new DateTime(startDate)))) {
                    payTable = currentPayTable;
                    break;
                }
            }
        } else if (payTableQueryResults.size() == 1)
            payTable = payTableQueryResults.get(0);

        return new PayTableResponseWrapper(payGroupAreaQueryResults, payTable, functions);

    }

    public List<OrganizationLevelPayGroupAreaDTO> getOrganizationLevelWisePayGroupAreas(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.level.id.notFound", countryId);

        }
        return payTableGraphRepository.getOrganizationLevelWisePayGroupAreas(countryId);
    }

    public PayTableResponse createPayTable(Long countryId, PayTableDTO payTableDTO) {
        logger.info(payTableDTO.toString());
        Level level = countryGraphRepository.getLevel(countryId, payTableDTO.getLevelId());
        if (!Optional.ofNullable(level).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.paytable.level.notfound");

        }

        Boolean isAlreadyExists = payTableGraphRepository.
                checkPayTableNameAlreadyExitsByName(countryId, -1L, "(?i)" + payTableDTO.getName().trim());
        if (isAlreadyExists) {
            exceptionService.duplicateDataException("message.payTable.name.alreadyExist", payTableDTO.getName(), countryId);

        }
        PayTableResponse payTableToValidate = payTableGraphRepository.findPayTableByOrganizationLevel(payTableDTO.getLevelId(), -1L);
        // if any payTable is found then only validate
        if (Optional.ofNullable(payTableToValidate).isPresent())
            validatePayLevel(payTableToValidate, payTableDTO.getStartDateMillis(), payTableDTO.getEndDateMillis());
        PayTable payTable = new PayTable(payTableDTO.getName().trim(), payTableDTO.getShortName(), payTableDTO.getDescription(), level, payTableDTO.getStartDateMillis(), payTableDTO.getEndDateMillis(), payTableDTO.getPaymentUnit(), true);
        payTableGraphRepository.save(payTable);
        PayTableResponse payTableResponse = new PayTableResponse(payTable.getName(), payTable.getShortName(), payTable.getDescription(), payTable.getStartDateMillis().getTime(), (payTable.getEndDateMillis() != null) ? payTable.getEndDateMillis().getTime() : null, payTable.isPublished(), payTable.getPaymentUnit(), payTable.isEditable());
        payTableResponse.setId(payTable.getId());
        payTableResponse.setLevel(level);
        return payTableResponse;
    }


    private void validatePayLevel(PayTableResponse payTableToValidate, Date startDateMillis, Date endDateMillis) {
        if (payTableToValidate.getEndDateMillis() != null) {
            logger.info("new  startDate{}", new DateTime(startDateMillis).toLocalDate() + "  End date " + new DateTime(payTableToValidate.getEndDateMillis()).toLocalDate());
            Days days = Days.daysBetween(new DateTime(startDateMillis).toLocalDate(), new DateTime(payTableToValidate.getEndDateMillis()).toLocalDate());
            logger.info("difference in days" + days);
            if (days.getDays() != -1) {
                exceptionService.actionNotPermittedException("message.startdate.allowed", new DateTime(payTableToValidate.getEndDateMillis() + ONE_DAY));

            }
        } else {
            exceptionService.actionNotPermittedException("message.paytable.alreadyactive", payTableToValidate.getName(), payTableToValidate.getId());

        }
    }

    private void prepareDates(PayTable payTable, PayTableUpdateDTO payTableDTO) {
        if (!payTable.getStartDateMillis().equals(payTableDTO.getStartDateMillis())) {
            // The start date is modified Now We need to compare is it less than today
            if (new DateTime(payTableDTO.getStartDateMillis()).isBefore(new DateTime(DateUtil.getCurrentDate()))) {
                exceptionService.actionNotPermittedException("message.startdate.lessthan");

            }
            payTable.setStartDateMillis(payTableDTO.getStartDateMillis());
        }
        // End date
        // If already end date was set  but now no value so we are removing
        if (!Optional.ofNullable(payTableDTO.getEndDateMillis()).isPresent() && Optional.ofNullable(payTable.getEndDateMillis()).isPresent()) {
            payTable.setEndDateMillis(null);

        }

        // If already not present now its present    Previous its absent
        else if (!Optional.ofNullable(payTable.getEndDateMillis()).isPresent() && Optional.ofNullable(payTableDTO.getEndDateMillis()).isPresent()) {
            if (new DateTime(payTableDTO.getEndDateMillis()).isBefore(new DateTime(DateUtil.getCurrentDate()))) {
                exceptionService.actionNotPermittedException("message.endtdate.lessthan");

            }
            payTable.setEndDateMillis(payTableDTO.getEndDateMillis());
        }

        // If already present and still present // NOw checking are they same or different
        else if (Optional.ofNullable(payTable.getEndDateMillis()).isPresent() && Optional.ofNullable(payTableDTO.getEndDateMillis()).isPresent()) {
            if (!payTable.getEndDateMillis().equals(payTableDTO.getEndDateMillis())) {//The end date is modified Now We need to compare is it less than today
                if (new DateTime(payTableDTO.getEndDateMillis()).isBefore(new DateTime(DateUtil.getCurrentDate()))) {
                    exceptionService.actionNotPermittedException("message.endtdate.lessthan");

                }
                payTable.setEndDateMillis(payTableDTO.getEndDateMillis());
            }
        }

    }

    // update basic detail of pay Table we are not making any copy as this time  as this does not impact any calculation
    public PayTableResponse updatePayTable(Long countryId, Long payTableId, PayTableUpdateDTO payTableDTO) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.paytable.id.notfound");

        }
        // if  name or short name is changes then only we are checking its name existance
        // skipped to current pay table it is checking weather any other payTable has same name or short name in current organization level
        if (!payTableDTO.getName().trim().equalsIgnoreCase(payTable.getName())) {
            Boolean isAlreadyExists = payTableGraphRepository.
                    checkPayTableNameAlreadyExitsByName(countryId, payTableId, "(?i)" + payTableDTO.getName().trim());
            if (isAlreadyExists) {
                exceptionService.duplicateDataException("message.payTable.name.alreadyExist", payTableDTO.getName(), countryId);

            }
        }
        PayTableResponse payTableToValidate = payTableGraphRepository.findPayTableByOrganizationLevel(payTableDTO.getLevelId(), -1L);

        validatePayLevel(payTable, payTableToValidate, payTableDTO);

        payTable.setName(payTableDTO.getName().trim());
        payTable.setShortName(payTableDTO.getShortName());
        payTable.setDescription(payTableDTO.getDescription());
        payTable.setPaymentUnit(payTableDTO.getPaymentUnit());
        prepareDates(payTable, payTableDTO);
        payTableGraphRepository.save(payTable);
        PayTableResponse payTableResponse = new PayTableResponse(payTable.getName(), payTable.getShortName(), payTable.getDescription(), payTable.getStartDateMillis().getTime(), payTable.getEndDateMillis() != null ? payTable.getEndDateMillis().getTime() : null, payTable.isPublished(), payTable.getPaymentUnit(), payTable.isEditable());
        payTableResponse.setId(payTable.getId());
        return payTableResponse;
    }

    private void validatePayLevel(PayTable payTableFromDatabase, PayTableResponse payTableToValidate, PayTableUpdateDTO payTableDTO) {
        // user is updating any mid table so in this table we wont allow to edit the dates
        if (!payTableToValidate.getId().equals(payTableFromDatabase.getId())) {
            logger.info("new  startDate{}" + payTableFromDatabase.getStartDateMillis() + "   " + payTableDTO.getStartDateMillis()
                    + " " + payTableFromDatabase.getEndDateMillis() + "  " + payTableDTO.getEndDateMillis());
            if (!payTableFromDatabase.getStartDateMillis().equals(payTableDTO.getStartDateMillis()) || !payTableFromDatabase.getEndDateMillis().equals(payTableDTO.getEndDateMillis())) {
                exceptionService.actionNotPermittedException("message.paytable.datechange.notallowed", payTableDTO.getName().trim());

            }
        } else if (!payTableFromDatabase.getStartDateMillis().equals(payTableDTO.getStartDateMillis())) {
            exceptionService.actionNotPermittedException("message.startdate.noteditable", payTableToValidate.getName());

        }
    }

    public List<PayGradeResponse> addPayGradeInPayTable(Long payTableId, PayGradeDTO payGradeDTO) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.paytable.id.notfound");

        }
        Boolean isAlreadyExists = payTableGraphRepository.checkPayGradeLevelAlreadyExists(payTableId, payGradeDTO.getPayGradeLevel());
        if (isAlreadyExists) {
            exceptionService.duplicateDataException("message.paygrade.level.alreadyexist", payGradeDTO.getPayGradeLevel());

        }
        // payTable is not published
        List<PayGradeResponse> payGradesData = new ArrayList<>();
        if (!payTable.isPublished()) {
            payGradesData.add(addPayGradeInCurrentPayTable(payTable, payGradeDTO));
        } else {
            payGradesData = createCopyOfPayTableAndAddPayGrade(payTable, payGradeDTO, null, false);
        }
        return payGradesData;

    }

    private List<PayGradeResponse> createCopyOfPayTableAndAddPayGrade(PayTable payTable, PayGradeDTO payGradeDTO, List<PayGradePayGroupAreaRelationShip> payGradesPayGroupAreaRelationShips, boolean updateValues) {
        List<PayGradeResponse> payGradeResponses = new ArrayList<>();
        PayTable copiedPayTable = new PayTable();
        BeanUtils.copyProperties(payTable, copiedPayTable);
        copiedPayTable.setId(null);
        copiedPayTable.setPayTable(payTable);
        copiedPayTable.setPayGrades(null);
        if (payGradeDTO != null) {
            payGradeResponses.add(addPayGradeInCurrentPayTable(copiedPayTable, payGradeDTO));
        }
        payTable.setHasTempCopy(true);
        copiedPayTable.setPublished(false);
        payTableGraphRepository.save(copiedPayTable);
        // copying all previous and then adding in pay Table as well.
        List<PayGrade> payGradesObjects = new ArrayList<>();
        for (PayGrade currentPayGrade : payTable.getPayGrades()) {
            PayGrade newPayGrade = new PayGrade(currentPayGrade.getPayGradeLevel(), false);
            List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips = payGradesPayGroupAreaRelationShips == null ? new ArrayList<>() : payGradesPayGroupAreaRelationShips;
            if (!updateValues) {
                HashSet<PayTableMatrixQueryResult> payTableMatrix = payGradeGraphRepository.getPayGradeMatrixByPayGradeId(currentPayGrade.getId());

                payTableMatrix.forEach(currentObj -> {
                    PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip
                            = new PayGradePayGroupAreaRelationShip(newPayGrade, new PayGroupArea(currentObj.getPayGroupAreaId(), currentObj.getPayGroupAreaName()), currentObj.getPayGroupAreaAmount());
                    payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);
                });
            }
            if (payGradesPayGroupAreaRelationShips != null) {
                payTableRelationShipGraphRepository.saveAll(payGradePayGroupAreaRelationShips);
                Map<Long, Long> payGradeMap = payGradePayGroupAreaRelationShips.stream().collect(Collectors.toMap(k -> k.getPayGrade().getPayGradeLevel(), k -> k.getPayGrade().getId()));
                newPayGrade.setId(payGradeMap.get(newPayGrade.getPayGradeLevel()));
            }
            payGradesObjects.add(newPayGrade);
            PayGradeResponse payGradeResponse =
                    new PayGradeResponse(copiedPayTable.getId(), newPayGrade.getPayGradeLevel(), newPayGrade.getId(), getPayGradeResponse(payGradePayGroupAreaRelationShips), newPayGrade.isPublished());
            payGradeResponses.add(payGradeResponse);
        }
        // Adding new Grade in PayTable
        copiedPayTable.setPayGrades(payGradesObjects);
        payTableGraphRepository.save(copiedPayTable);

        return payGradeResponses;
    }


    private PayGradeResponse addPayGradeInCurrentPayTable(PayTable payTable, PayGradeDTO payGradeDTO) {
        Set<Long> payGroupAreasId = payGradeDTO.getPayGroupAreas().stream().map(PayGroupAreaDTO::getPayGroupAreaId).collect(Collectors.toSet());

        List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllByIds(payGroupAreasId);
        if (payGroupAreas.size() != payGroupAreasId.size()) {
            exceptionService.dataNotMatchedException("message.paygrouparea.unabletoget");

        }

        PayGrade payGrade = new PayGrade(payGradeDTO.getPayGradeLevel());
        if (Optional.ofNullable(payTable.getPayGrades()).isPresent()) {
            payTable.getPayGrades().add(payGrade);
        } else {
            List<PayGrade> payGrades = new ArrayList<>();
            payGrades.add(payGrade);
            payTable.setPayGrades(payGrades);
        }
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
            exceptionService.dataNotFoundByIdException("message.paytable.id.notfound");

        }
        return payTableGraphRepository.getPayGradesByPayTableId(payTableId);
    }

    public boolean removePayGradeInPayTable(Long payTableId, Long payGradeId) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            exceptionService.dataNotFoundByIdException("message.paytable.id.notfound");

        }
        if (payTable.isPublished()) {
            exceptionService.actionNotPermittedException("message.paytable.alreadypublished");

        }
        if (!Optional.ofNullable(payTable.getPayGrades()).isPresent() || payTable.getPayGrades().isEmpty()) {
            exceptionService.dataNotFoundByIdException("message.paytable.id.notfound");

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
            exceptionService.dataNotFoundByIdException("message.paytable.id.notfound");

        }
        if (payTable.isPublished()) {
            exceptionService.actionNotPermittedException("message.paytable.alreadypublished");

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
        payGradeResponses.add(payGradeResponse);
        return payGradeResponses;
    }

    public List<PayGradeResponse> updatePayGradeInPayTable(Long payTableId, Long payGradeId, PayGradeDTO payGradeDTO) {
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            exceptionService.dataNotFoundByIdException("message.paytable.id.notfound");

        }
        PayGrade payGrade = null;
        for (PayGrade currentPayGrade : payTable.getPayGrades()) {
            if (currentPayGrade.getId().equals(payGradeId)) {
                payGrade = currentPayGrade;
                break;
            }
        }
        if (!Optional.ofNullable(payGrade).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.paygrade.id.notfound", payGradeId);

        }
        List<PayGradeResponse> payGradeResponses;
        // user is updating in a unpublished payTable
        payGradeResponses = (!payTable.isPublished()) ? updatePayGradeInUnpublishedPayTable(payTable, payGradeDTO, payGrade) :
                updatePayGradeInPublishedPayTable(payTable, payGradeDTO, payGradeId);

        return payGradeResponses;
    }


    private List<PayGradeResponse> updatePayGradeInPublishedPayTable(PayTable payTable, PayGradeDTO payGradeDTO, Long payGradeId) {
        List<PayGradeResponse> payGradeResponses = new ArrayList<>();
        List<PayGrade> payGradesObjects = new ArrayList<>();
        // creating a new PayTable
        PayTable payTableByMapper = new PayTable();
        BeanUtils.copyProperties(payTable, payTableByMapper);
        payTableByMapper.setId(null);
        payTableByMapper.setPayTable(payTable);
        payTableByMapper.setPayGrades(null);
        payTableByMapper.setPublished(false);
        payTable.setHasTempCopy(true);
        payTable.setEditable(false);
        payTableByMapper.setHasTempCopy(false);
        payTableGraphRepository.save(payTableByMapper);
        for (PayGrade currentPayGrade : payTable.getPayGrades()) {
            PayGrade newPayGrade = new PayGrade(currentPayGrade.getPayGradeLevel(), false);
            List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips = new ArrayList<>();

            if (payGradeDTO.getPayGradeId().equals(currentPayGrade.getId())) {
                // user has changed the value in  this pay Grade area of payTable
                Set<Long> payGroupAreasId = payGradeDTO.getPayGroupAreas().stream().map(PayGroupAreaDTO::getPayGroupAreaId).collect(Collectors.toSet());
                List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllByIds(payGroupAreasId);

                for (PayGroupAreaDTO currentPayGroupArea : payGradeDTO.getPayGroupAreas()) {
                    PayGroupArea payGroupArea = payGroupAreas.stream().filter(payGroupArea1 -> payGroupArea1.getId().equals(currentPayGroupArea.getPayGroupAreaId())).findFirst().get();
                    PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip
                            = new PayGradePayGroupAreaRelationShip(newPayGrade, payGroupArea, currentPayGroupArea.getPayGroupAreaAmount());
                    payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);

                }
            } else {
                HashSet<PayTableMatrixQueryResult> payTableMatrix = payGradeGraphRepository.getPayGradeMatrixByPayGradeId(currentPayGrade.getId());
                payTableMatrix.forEach(currentObj -> {
                    PayGradePayGroupAreaRelationShip payGradePayGroupAreaRelationShip
                            = new PayGradePayGroupAreaRelationShip(newPayGrade, new PayGroupArea(currentObj.getPayGroupAreaId(), currentObj.getPayGroupAreaName()), currentObj.getPayGroupAreaAmount());
                    payGradePayGroupAreaRelationShips.add(payGradePayGroupAreaRelationShip);
                });
            }
            payTableRelationShipGraphRepository.saveAll(payGradePayGroupAreaRelationShips);
            payGradesObjects.add(newPayGrade);
            PayGradeResponse payGradeResponse =
                    new PayGradeResponse(payTableByMapper.getId(), newPayGrade.getPayGradeLevel(), newPayGrade.getId(), getPayGradeResponse(payGradePayGroupAreaRelationShips), newPayGrade.isPublished());
            payGradeResponses.add(payGradeResponse);

        }
        payTableByMapper.setPayGrades(payGradesObjects);
        payTableGraphRepository.save(payTableByMapper);
        return payGradeResponses;
    }

    public List<PayTable> publishPayTable(Long payTableId, Long publishedDateMillis) {
//        if(publishedDateMillis<DateUtils.getCurrentMillis()){
//            exceptionService.actionNotPermittedException("message.startdate.lessthan");
//        }
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        if (!Optional.ofNullable(payTable).isPresent() || payTable.isDeleted()) {
            exceptionService.dataNotFoundByIdException("message.paytable.id.notfound");

        }
        if (payTable.isPublished()) {
            exceptionService.actionNotPermittedException("message.paytable.published");

        }
        if (CollectionUtils.isEmpty(payTable.getPayGrades())) {
            exceptionService.actionNotPermittedException("message.paygrade.absent");
        }
        List<PayTable> response = new ArrayList<>();

        PayTable parentPayTable = payTableGraphRepository.getPermanentPayTableByPayTableId(payTableId);
        logger.debug(new DateTime(payTable.getStartDateMillis()).toLocalDate() + "----" + (new DateTime(publishedDateMillis).toLocalDate()));
        if (Optional.ofNullable(parentPayTable).isPresent()) {
            payTableGraphRepository.changeStateOfRelationShip(parentPayTable.getId(), publishedDateMillis - ONE_DAY);
            parentPayTable.setEndDateMillis(new Date(publishedDateMillis - ONE_DAY));
            parentPayTable.setHasTempCopy(false);
            parentPayTable.setPayTable(null);
            response.add(parentPayTable);

        } else if (!new DateTime(payTable.getStartDateMillis()).toLocalDate().equals(new DateTime(publishedDateMillis).toLocalDate())) {
            exceptionService.actionNotPermittedException("message.paytable.published.samedate");

        }
        payTable.setPayTable(null);
        payTable.setPublished(true);
        payTable.setStartDateMillis(new Date(publishedDateMillis));

        payTable.getPayGrades().forEach(currentPayGrade -> currentPayGrade.setPublished(true));
        payTableGraphRepository.save(payTable);
        if(payTable.getPercentageValue()!=null && parentPayTable!=null){
            functionalPaymentService.updateAmountInFunctionalTable(parentPayTable.getId(),payTable.getStartDateMillis(),payTable.getEndDateMillis(),payTable.getPercentageValue());
        }

        response.add(payTable);
        return response;
    }


    public List<PayTableResponse> getPayTablesByOrganizationLevel(Long organizationLevelId) {

        return payTableGraphRepository.findActivePayTablesByOrganizationLevel(organizationLevelId);

    }

    public PayTableUpdateDTO updatePayTableAmountByPercentage(Long payTableId, PayTableDTO payTableDTO) {
        if (payTableDTO.getPercentageValue() == null || payTableDTO.getPercentageValue().equals(new BigDecimal(0))) {
            exceptionService.actionNotPermittedException("exception.null.percentageValue");
        }
        PayTable payTable = payTableGraphRepository.findOne(payTableId);
        List<PayGradeResponse> payGradeResponses=null;
        payTable.setPercentageValue(payTableDTO.getPercentageValue());
        List<PayGradePayGroupAreaRelationShip> payGradePayGroupAreaRelationShips = ObjectMapperUtils.copyPropertiesOfListByMapper(payTableRelationShipGraphRepository.findAllByPayTableId(payTableId), PayGradePayGroupAreaRelationShip.class);
            if (CollectionUtils.isNotEmpty(payGradePayGroupAreaRelationShips)) {
                for (PayGradePayGroupAreaRelationShip current : payGradePayGroupAreaRelationShips) {
                    if (current.getPayGroupAreaAmount() != null) {
                        BigDecimal valueToAdd = current.getPayGroupAreaAmount().multiply(payTableDTO.getPercentageValue()).divide(new BigDecimal(100));
                        current.setPayGroupAreaAmount(current.getPayGroupAreaAmount().add(valueToAdd));
                        if(payTable.isPublished()){
                            current.setId(null);
                            current.getPayGrade().setId(null);
                        }

                    }
                }
                if(payTable.isPublished()){
                    payGradeResponses = createCopyOfPayTableAndAddPayGrade(payTable, null, payGradePayGroupAreaRelationShips, true);
                }
                else {
                    payTableRelationShipGraphRepository.saveAll(payGradePayGroupAreaRelationShips);
                    payTableGraphRepository.save(payTable);

                }
            }
            Long id=CollectionUtils.isEmpty(payGradeResponses)?payTable.getId():payGradeResponses.get(0).getPayTableId();
            return new PayTableUpdateDTO(id,payTable.getName());


    }


}

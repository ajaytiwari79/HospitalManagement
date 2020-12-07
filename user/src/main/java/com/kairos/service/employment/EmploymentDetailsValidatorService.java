package com.kairos.service.employment;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.user.country.experties.FunctionsDTO;
import com.kairos.dto.user.staff.employment.EmploymentDTO;
import com.kairos.persistence.model.country.functions.FunctionWithAmountQueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.pay_table.PayTable;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.employment.Employment;
import com.kairos.persistence.model.user.employment.EmploymentLine;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.ExpertiseLine;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.country.functions.FunctionGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayTableGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.AsynchronousService;
import com.kairos.service.exception.ExceptionService;
import org.joda.time.Interval;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;
import static com.kairos.constants.UserMessagesConstants.*;

@Service
public class EmploymentDetailsValidatorService {
    @Inject
    private FunctionGraphRepository functionGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private AsynchronousService asynchronousService;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private PayTableGraphRepository payTableGraphRepository;
    @Inject
    private SeniorityLevelService seniorityLevelService;

    public List<FunctionWithAmountQueryResult> findAndValidateFunction(EmploymentDTO employmentDTO) {
        List<Long> funIds = employmentDTO.getFunctions().stream().map(FunctionsDTO::getId).collect(Collectors.toList());
        List<FunctionWithAmountQueryResult> functions = functionGraphRepository.getFunctionsByExpertiseAndSeniorityLevelAndIds
                (employmentDTO.getUnitId(), employmentDTO.getExpertiseId(), employmentDTO.getSeniorityLevelId(), employmentDTO.getStartDate().toString(),
                        funIds);
        return functions;
    }

    public boolean validateEmploymentWithExpertise(List<Employment> employments, EmploymentDTO employmentDTO) {

        LocalDate employmentStartDate = employmentDTO.getStartDate();
        LocalDate employmentEndDate = employmentDTO.getEndDate();
        employments.forEach(employment -> {
            if (employment.getEndDate() != null) {
                if (employmentStartDate.isBefore(employment.getEndDate()) && employmentStartDate.isAfter(employment.getStartDate())) {
                    exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_POSITIONCODE_ALREADYEXIST_WITHVALUE, employmentEndDate, employment.getStartDate());
                }
                if (employmentEndDate != null) {
                    validateInterval(employmentStartDate, employmentEndDate, employment);
                } else {
                    if (employmentStartDate.isBefore(employment.getEndDate())) {
                        exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_POSITIONCODE_ALREADYEXIST_WITHVALUE, employmentEndDate, employment.getEndDate());
                    }
                }
            } else {
                validateDates(employmentEndDate, employment);
            }
        });

        return true;
    }

    private void validateInterval(LocalDate employmentStartDate, LocalDate employmentEndDate, Employment employment) {
        Interval previousInterval = new Interval(DateUtils.getDateFromEpoch(employment.getStartDate()), DateUtils.getDateFromEpoch(employment.getEndDate()));
        Interval interval = new Interval(DateUtils.getDateFromEpoch(employmentStartDate), DateUtils.getDateFromEpoch(employmentEndDate));
        if (previousInterval.overlaps(interval))
            exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_POSITIONCODE_ALREADYEXIST);
    }

    private void validateDates(LocalDate employmentEndDate, Employment employment) {
        if (employmentEndDate != null) {
            if (employmentEndDate.isAfter(employment.getStartDate())) {
                exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_POSITIONCODE_ALREADYEXIST_WITHVALUE, employmentEndDate, employment.getStartDate());

            }
        } else {
            exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_POSITIONCODE_ALREADYEXIST);
        }
    }

    public Employment prepareAndValidateEmployment(Employment employment, EmploymentDTO employmentDTO) throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> done = setDefaultData(employmentDTO, employment);
        CompletableFuture.allOf(done).join();
        employment.setStartDate(employmentDTO.getStartDate());
        if (Optional.ofNullable(employmentDTO.getEndDate()).isPresent()) {
            if (employmentDTO.getStartDate().isAfter(employmentDTO.getEndDate())) {
                exceptionService.actionNotPermittedException(MESSAGE_STARTDATE_NOTLESSTHAN_ENDDATE);
            }
            if (!Optional.ofNullable(employmentDTO.getReasonCodeId()).isPresent()) {
                exceptionService.actionNotPermittedException(MESSAGE_REGION_ENDDATE);
            }
            employment.setReasonCodeId(employmentDTO.getReasonCodeId());
            employment.setEndDate(employmentDTO.getEndDate());
        }

        if (Optional.ofNullable(employmentDTO.getLastWorkingDate()).isPresent()) {
            if (employmentDTO.getStartDate().isAfter(employmentDTO.getLastWorkingDate())) {
                exceptionService.actionNotPermittedException(MESSAGE_LASTDATE_NOTLESSTHAN_STARTDATE);
            }
            employment.setLastWorkingDate(employmentDTO.getLastWorkingDate());
        }
        List<EmploymentLine> employmentLines=getEmploymentLines(employmentDTO, employment);
        employment.setEmploymentLines(employmentLines);

        return employment;
    }

    private CompletableFuture<Boolean> setDefaultData(EmploymentDTO employmentDTO, Employment employment) throws InterruptedException, ExecutionException {


        if (Optional.ofNullable(employmentDTO.getUnionId()).isPresent()) {
            Callable<Organization> organizationCallable = () -> organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(employmentDTO.getUnionId());
            Future<Organization> organizationFuture = asynchronousService.executeAsynchronously(organizationCallable);
            if (!Optional.ofNullable(organizationFuture.get()).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_UNION_NOTEXIST, employmentDTO.getUnionId());
            }
            employment.setUnion(organizationFuture.get());
        }

        Callable<Staff> staffCallable = () -> staffGraphRepository.findOne(employmentDTO.getStaffId());
        Future<Staff> staffFuture = asynchronousService.executeAsynchronously(staffCallable);
        if (!Optional.ofNullable(staffFuture.get()).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EMPLOYMENT_STAFF_NOTFOUND, employmentDTO.getStaffId());
        }
        employment.setStaff(staffFuture.get());
        return CompletableFuture.completedFuture(true);
    }

    private List<EmploymentLine> getEmploymentLines(EmploymentDTO employmentDTO, Employment employment) {
        List<EmploymentLine> employmentLines = new ArrayList<>();
        Expertise expertise = expertiseGraphRepository.findOne(employmentDTO.getExpertiseId(), 2);
        expertise.getExpertiseLines().sort(Comparator.comparing(ExpertiseLine::getStartDate));
        LocalDate startDateForLine = employmentDTO.getStartDate();
        LocalDate endDateForLine;
        for (ExpertiseLine expertiseLine : expertise.getExpertiseLines()) {
            DateTimeInterval expertiseLineInterval = new DateTimeInterval(expertiseLine.getStartDate(), expertiseLine.getEndDate());
            DateTimeInterval employmentInterval = new DateTimeInterval(employmentDTO.getStartDate(), employmentDTO.getEndDate());
            if (expertiseLineInterval.overlaps(employmentInterval)) {
                List<PayTable> payTables = payTableGraphRepository.findAllActivePayTable(expertise.getOrganizationLevel().getId(), expertiseLine.getStartDate().toString(), expertiseLine.getEndDate() == null ? null : expertiseLine.getEndDate().toString(),employmentDTO.getStartDate().toString());
                if (isCollectionEmpty(payTables)) {
                    addEmploymentLines(employmentDTO, employmentLines, expertiseLine, employmentDTO.getStartDate().isAfter(expertiseLine.getStartDate()) ? employmentDTO.getStartDate() : expertiseLine.getStartDate(), expertiseLine.getEndDate());
                } else {
                    payTables.sort(Comparator.comparing(PayTable::getStartDateMillis));
                    for (PayTable payTable : payTables) {
                        startDateForLine = getStartDate(startDateForLine, expertiseLine, payTable);
                        endDateForLine = getEndDate(expertiseLine, payTable);
                        addEmploymentLines(employmentDTO, employmentLines, expertiseLine, startDateForLine, endDateForLine);
                        if (endDateForLine != null) {
                            startDateForLine = endDateForLine.plusDays(1);
                        }
                    }
                }
            }
        }
        employment.setExpertise(expertise);
        if(employmentLines.size()==1){
            employmentLines.get(0).setEndDate(employment.getEndDate());
        }
        return employmentLines;
    }

    private void addEmploymentLines(EmploymentDTO employmentDTO, List<EmploymentLine> employmentLines, ExpertiseLine expertiseLine, LocalDate startDate, LocalDate endDate) {
        employmentLines.add(EmploymentLine.builder()
                .seniorityLevel(seniorityLevelService.getSeniorityLevelByStaffAndExpertise(employmentDTO.getStaffId(), expertiseLine, employmentDTO.getExpertiseId()))
                .startDate(startDate)
                .endDate(endDate)
                .totalWeeklyMinutes(employmentDTO.getTotalWeeklyMinutes() + (employmentDTO.getTotalWeeklyHours() * 60))
                .fullTimeWeeklyMinutes(expertiseLine.getFullTimeWeeklyMinutes())
                .workingDaysInWeek(expertiseLine.getNumberOfWorkingDaysInWeek())
                .avgDailyWorkingHours(employmentDTO.getAvgDailyWorkingHours())
                .hourlyCost(employmentDTO.getHourlyCost())
                .build());
    }

    private LocalDate getStartDate(LocalDate startDateForLine, ExpertiseLine expertiseLine, PayTable payTable) {
        return expertiseLine.getStartDate().isBefore(startDateForLine) ? startDateForLine : payTable.getStartDateMillis().isAfter(expertiseLine.getStartDate()) ? payTable.getStartDateMillis() : expertiseLine.getStartDate();
    }

    private LocalDate getEndDate(ExpertiseLine expertiseLine, PayTable payTable) {
        if (expertiseLine.getEndDate() == null && payTable.getEndDateMillis() != null) {
            return payTable.getEndDateMillis();
        } else if (expertiseLine.getEndDate() != null && payTable.getEndDateMillis() != null) {
            return payTable.getEndDateMillis().isBefore(expertiseLine.getEndDate()) ? payTable.getEndDateMillis() : expertiseLine.getEndDate();
        }
        return expertiseLine.getEndDate();
    }
}

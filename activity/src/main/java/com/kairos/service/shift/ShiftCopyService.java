package com.kairos.service.shift;

import com.kairos.commons.service.locale.LocaleService;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.staff.employment.StaffEmploymentUnitDataWrapper;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.TimeSlotType;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_slot.TimeSlotRepository;
import com.kairos.persistence.repository.unit_settings.ActivityConfigurationRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.day_type.DayTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.pay_out.PayOutService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.wrapper.ShiftResponseDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.NO_CONFLICTS;
import static com.kairos.service.shift.ShiftValidatorService.convertMessage;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getValidDays;
import static java.util.stream.Collectors.groupingBy;

/**
 * CreatedBy vipulpandey on 28/11/18
 **/
@Service
public class ShiftCopyService extends MongoBaseService {

    public static final String ERROR = "error";
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActivityMongoRepository activityRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private BreakSettingMongoRepository breakSettingMongoRepository;
    @Inject
    private ShiftBreakService shiftBreakService;
    @Inject
    private ShiftValidatorService shiftValidatorService;
    @Inject
    private LocaleService localeService;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private ShiftService shiftService;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private ActivityConfigurationRepository activityConfigurationRepository;
    @Inject
    private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private TimeBankService timeBankService;
    @Inject
    private PayOutService payOutService;
    @Inject
    private PhaseService phaseService;
    @Inject
    private DayTypeService dayTypeService;
    @Inject
    private TimeSlotRepository timeSlotRepository;

}

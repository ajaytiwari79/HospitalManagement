package com.kairos.service.shift;

import com.kairos.commons.service.locale.LocaleService;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_slot.TimeSlotMongoRepository;
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
import org.springframework.stereotype.Service;

import javax.inject.Inject;

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
    private TimeSlotMongoRepository timeSlotMongoRepository;

}

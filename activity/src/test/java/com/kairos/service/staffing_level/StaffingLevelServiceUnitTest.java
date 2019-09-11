package com.kairos.service.staffing_level;
/*
 *Created By Pavan on 16/8/18
 *
 */

//@RunWith(MockitoJUnitRunner.class)
public class StaffingLevelServiceUnitTest {
  /*  @InjectMocks
    private StaffingLevelService staffingLevelService;

    //mocking all dependency
    @Mock
    private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Mock
    private PhaseService phaseService;
    @Mock
    private EnvConfig envConfig;
    @Mock
    ActivityMongoRepository activityMongoRepository;
    StaffingLevel staffingLevel=null;

    @Before
    public void setUp() throws Exception {
        Duration duration=new Duration(LocalTime.MIN,LocalTime.MAX);
        StaffingLevelSetting staffingLevelSetting=new StaffingLevelSetting(15,duration);
        LocalDate date = LocalDate.now();
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int currentWeekCount = date.get(woy);
        staffingLevel=new StaffingLevel(DateUtils.getDate(),1,new Long("2567"),new BigInteger("1"),staffingLevelSetting);
        List<StaffingLevelInterval> StaffingLevelIntervals=new ArrayList<>();
        int startTimeCounter=0;
        LocalTime startTime=LocalTime.MIN;
        for(int i=0;i<=95;i++){
            StaffingLevelInterval staffingLevelInterval=new StaffingLevelInterval(i,0,0,new Duration(startTime.plusMinutes(startTimeCounter),
                    startTime.plusMinutes(startTimeCounter+=15)));

            staffingLevelInterval.setAvailableNoOfStaff(0);
            StaffingLevelIntervals.add(staffingLevelInterval);
        }
        staffingLevel.setPresenceStaffingLevelInterval(StaffingLevelIntervals);

    }


    @After
    public void tearDown() throws Exception {

    }*/

}

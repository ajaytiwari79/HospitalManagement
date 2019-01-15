package com.planner.service.shiftPlanningService;

public class ShiftPlanningServiceUnitTest {

   /* @Test
    public void getRecomendationShiftPlanningDTOAsJson(){
        RecomendationPlanningDTO planningDTO = new RecomendationPlanningDTO();
        List<StaffingLevelDto> staffingLevels = new ArrayList<>();
        for(int j=0;j<3;j++) {
            StaffingLevelDto staffingLevel = new StaffingLevelDto();
            Integer intervalMins = 15;
            staffingLevel.setDate(new LocalDate().plusDays(j));//new DateTime().plusDays(j).withTimeAtStartOfDay().toDate()
            staffingLevel.setId(new BigInteger(String.valueOf(j)));
            DateTime dateTime = new DateTime().plusDays(j);
            staffingLevel.setIntervalMinutes(intervalMins);
            List<StaffingLevelTimeSlotDTO> intervals = new ArrayList<>();
            int[][] minMaxHours = getMinMaxWorkHours(intervalMins);
            StaffingLevelActivityTypeDTO[] activityTypes = getActivityTypes(intervalMins);
            IntStream.rangeClosed(0, (1440 / intervalMins) - 1).forEachOrdered(i -> {
                intervals.add(new StaffingLevelTimeSlotDTO(dateTime.withTimeAtStartOfDay().plusMinutes(i * intervalMins).toDate(), dateTime.withTimeAtStartOfDay().plusMinutes((i + 1) * intervalMins).toDate(),
                        minMaxHours[i][0], minMaxHours[i][1], null, activityTypes[i] == null ? null : new HashSet(Arrays.asList(activityTypes[i]))));
            });
            staffingLevel.setIntervals(intervals);
            staffingLevels.add(staffingLevel);
        }
        planningDTO.setStaffingLevelDTOS(staffingLevels);
        planningDTO.setSolverConfigId("47a5c859-a20d-40c3-9d94-53420befd966");
        planningDTO.setEmployeeId(45455l);
        planningDTO.setPreferedShiftDTOS(generateShiftForAssignments());
        planningDTO.setEndTo(new DateTime().plusDays(3).toDate());
        planningDTO.setStartFrom(new DateTime().toDate());
        toJson(planningDTO);
    }


    private void toJson(RecomendationPlanningDTO planningDTO){
        ObjectMapper mapper = new ObjectMapper();
        try {
            //mapper.writerWithDefaultPrettyPrinter();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File("optaplanner-shiftplanning/src/main/resources/data/recomendation problem_with staffinglevel.json"),planningDTO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[][] getMinMaxWorkHours(Integer intervalMins){
        int[][] minMaxHours= new int[1440/intervalMins][2];
        if(intervalMins==15){
            IntStream.rangeClosed(32,79).forEachOrdered(i->{
                minMaxHours[i][0]=1;
                minMaxHours[i][1]=2;
            });
        }
        return minMaxHours;
    }

    public List<PreferedShiftDTO> generateShiftForAssignments() {
        List<PreferedShiftDTO> shiftList = new ArrayList<>();
        PreferedShiftDTO sa= new PreferedShiftDTO();
        sa.setEmployeeId(45455l);
        sa.setId("0");
        shiftList.add(sa);


        PreferedShiftDTO sa1= new PreferedShiftDTO();
        sa1.setEmployeeId(45455l);
        sa1.setId("1");
        //sa1.setCreatedByStaff(true);
        shiftList.add(sa1);

        PreferedShiftDTO sa2= new PreferedShiftDTO();
        sa2.setEmployeeId(45455l);
        sa2.setId("2");
        shiftList.add(sa2);

        PreferedShiftDTO sa3= new PreferedShiftDTO();
        sa3.setEmployeeId(45455l);
        sa3.setId("3");
        shiftList.add(sa3);
        generateShifts(shiftList);
        return shiftList;
    }

    private List<PreferedShiftDTO> generateShifts(List<PreferedShiftDTO> shifts){
        int start = 2;
        int end = 8;
        for(int i=0;i<=8;i++){
            PreferedShiftDTO shift = new PreferedShiftDTO();
            shift.setStart(new DateTime().minusHours(end).toDate());
            shift.setEnd(new DateTime().minusHours(start).toDate());
            shift.setLocked(true);
            shift.setEmployeeId(45455l);
            shift.setId(""+(i+4));
            start+=5;
            end+=5;
            shifts.add(shift);
        }
        return shifts;
    }

    private StaffingLevelActivityTypeDTO[] getActivityTypes(Integer intervalMins){
        StaffingLevelActivityTypeDTO[] staffingLevelActivityTypes= new StaffingLevelActivityTypeDTO[1440/intervalMins];
        StaffingLevelActivityTypeDTO staffingLevelActivityType= new StaffingLevelActivityTypeDTO(2, createSkillSet(), true);
        if(intervalMins==15){
            IntStream.rangeClosed(32,59).forEachOrdered(i->{
                //if(i==56) return;
                staffingLevelActivityTypes[i]= new StaffingLevelActivityTypeDTO(2, createSkillSet(), true);
            });
        }
        return staffingLevelActivityTypes;
    }

    private Set<Long> createSkillSet(){
        return Collections.singleton(666l);
    }*/

}
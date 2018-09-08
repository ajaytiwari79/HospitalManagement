package com.kairos.service.planner;
import com.kairos.persistence.model.task_demand.MonthlyFrequency;
import com.kairos.persistence.model.task_demand.TaskDemand;
import com.kairos.service.MongoBaseService;
import com.kairos.commons.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.time.ZoneId.systemDefault;

/**
 * Created by oodles on 19/7/17.
 */
@Service
@Transactional
public class RandomDateGeneratorService extends MongoBaseService {

    private static final Logger logger = LoggerFactory.getLogger(RandomDateGeneratorService.class);


    public List<Map<String, LocalDate>> getRandomDates(int numberOfWeeks, int visitCount, LocalDate createTaskFrom, boolean isWeekEnd, Date demandEndDate, List<Long> publicHolidayList, boolean skipTaskOnPublicHoliday) {

        LocalDate taskDemandEndDate = demandEndDate.toInstant().atZone(systemDefault()).toLocalDate();

        LocalDate dateAfterWeeks = createTaskFrom.plusWeeks(numberOfWeeks);

        //First get initial random dates, from date on which task to be created to date after adding number of weeks
        List<LocalDate> initialRandomDates = generateInitialRandomDates(createTaskFrom, dateAfterWeeks, visitCount, isWeekEnd, taskDemandEndDate, publicHolidayList, skipTaskOnPublicHoliday);

        List<Map<String, LocalDate>> randomDatesList = new ArrayList<>();

        LocalDate boundaryStartDate = createTaskFrom;
        LocalDate boundaryEndDate = boundaryStartDate.plusWeeks(numberOfWeeks).minusDays(1);

        //Add taskStartBoundaryDate and taskEndBoundaryDate to initial generated random dates.
        randomDatesList.addAll(addBoundaryDates(initialRandomDates, boundaryStartDate, boundaryEndDate));

        boundaryStartDate = createTaskFrom.plusWeeks(numberOfWeeks);
        boundaryEndDate = boundaryStartDate.plusWeeks(numberOfWeeks).minusDays(1);

        if(!boundaryEndDate.equals(taskDemandEndDate)){
            while (dateAfterWeeks.isBefore(taskDemandEndDate) || dateAfterWeeks.isEqual(taskDemandEndDate)) {


                List<LocalDate> nextRandomDates;
                if(publicHolidayList.isEmpty()){
                    nextRandomDates = addRepetitionsToRandomDates(initialRandomDates, numberOfWeeks, taskDemandEndDate);
                }else {
                    nextRandomDates = addRepetitionsToRandomDates(initialRandomDates, numberOfWeeks, taskDemandEndDate, publicHolidayList, skipTaskOnPublicHoliday, boundaryStartDate, boundaryEndDate, isWeekEnd);
                }

                randomDatesList.addAll(addBoundaryDates(nextRandomDates, boundaryStartDate, boundaryEndDate));
                initialRandomDates = nextRandomDates;

                dateAfterWeeks = dateAfterWeeks.plusWeeks(numberOfWeeks);
                boundaryStartDate = boundaryStartDate.plusWeeks(numberOfWeeks);
                boundaryEndDate = boundaryEndDate.plusWeeks(numberOfWeeks).minusDays(1);
            }
        }
        return randomDatesList;
    }

    /*
    This method generates initial random dates, which later used to creating repetitions for each date.
     */
    private List<LocalDate> generateInitialRandomDates(LocalDate createTaskFrom, LocalDate dateAfterWeeks, int visitCount, boolean isWeekEnd, LocalDate taskDemandEndDate, List<Long> publicHolidayList, boolean skipTaskOnPublicHoliday) {
        List<LocalDate> randomDateList = new ArrayList<>();
        Random random = new Random();
        int minDay = (int) createTaskFrom.toEpochDay();
        int maxDay = (int) dateAfterWeeks.toEpochDay();
        while (randomDateList.size() < visitCount) {
            long randomDay = minDay + random.nextInt(maxDay - minDay);
            LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
            Date date = Date.from(randomDate.atStartOfDay().atZone(systemDefault()).toInstant());

            if ((randomDate.isBefore(taskDemandEndDate) || randomDate.isEqual(taskDemandEndDate)) && (!randomDateList.contains(randomDate))) {
                if (isWeekEnd != true) {
                    if (!randomDate.getDayOfWeek().name().equals("SATURDAY") && !randomDate.getDayOfWeek().name().equals("SUNDAY")) {
                        if( (!publicHolidayList.contains(date.getTime())) ||  (publicHolidayList.contains(date.getTime()) && !skipTaskOnPublicHoliday )){
                            randomDateList.add(randomDate);
                        }
                    }
                } else {
                    if (randomDate.getDayOfWeek().name().equals("SATURDAY") || randomDate.getDayOfWeek().name().equals("SUNDAY")) {
                        randomDateList.add(randomDate);
                    }
                }
            }

        }
        return randomDateList;
    }

    /*
    This method receives initial set of random dates. Adds Number of weeks to each date for generating repetition till demand's end date.
     */
    private List<LocalDate> addRepetitionsToRandomDates(List<LocalDate> randomDates, int numberOfWeeks, LocalDate taskDemandEndDate) {
        List<LocalDate> repetitions = new ArrayList<>();
        for (LocalDate randomDate : randomDates) {
            LocalDate nextDate = randomDate.plusWeeks(numberOfWeeks);
            if (nextDate.isBefore(taskDemandEndDate) || nextDate.isEqual(taskDemandEndDate)) {
                repetitions.add(nextDate);
            }
        }
        return repetitions;
    }

    /*
    This method receives initial set of random dates. Adds Number of weeks to each date for generating repetition till demand's end date.
     */
    private List<LocalDate> addRepetitionsToRandomDates(List<LocalDate> randomDates, int numberOfWeeks, LocalDate taskDemandEndDate,
                                                        List<Long> publicHolidayList, boolean skipTaskOnPublicHoliday, LocalDate boundaryStartDate, LocalDate boundaryEndDate, boolean isWeekEnd) {
        List<LocalDate> repetitions = new ArrayList<>();
        for (LocalDate randomDate : randomDates) {
            LocalDate nextDate = randomDate.plusWeeks(numberOfWeeks);
            if (nextDate.isBefore(taskDemandEndDate) || nextDate.isEqual(taskDemandEndDate)) {
                Date date = Date.from(nextDate.atStartOfDay().atZone(systemDefault()).toInstant());
                if( ((!publicHolidayList.contains(date.getTime())) ||  (publicHolidayList.contains(date.getTime()) && !skipTaskOnPublicHoliday )) && !repetitions.contains(nextDate)){
                    repetitions.add(nextDate);
                }else{
                    nextDate = getRandomDateForSkippedRepitition(repetitions,publicHolidayList, boundaryStartDate, boundaryEndDate, isWeekEnd);
                    if(nextDate!=null) {
                        repetitions.add(nextDate);
                    }
                }
                //repetitions.add(nextDate);
            }
        }
        return repetitions;

    }

    private LocalDate getRandomDateForSkippedRepitition(List<LocalDate> repetitions, List<Long> publicHolidayList,  LocalDate boundaryStartDate, LocalDate boundaryEndDate, boolean isWeekEnd){

        long randomEpochDay = ThreadLocalRandom.current().longs(boundaryStartDate.toEpochDay(), boundaryEndDate.toEpochDay()).findAny().getAsLong();
        LocalDate randomDate = LocalDate.ofEpochDay(randomEpochDay);
        Date date = Date.from(randomDate.atStartOfDay().atZone(systemDefault()).toInstant());

        LocalDate localDate = null;

        if(!repetitions.contains(randomDate)) {
            if (isWeekEnd != true) {
                if (!randomDate.getDayOfWeek().name().equals("SATURDAY") && !randomDate.getDayOfWeek().name().equals("SUNDAY")) {
                    if (!publicHolidayList.contains(date.getTime())) {
                        localDate = randomDate;
                    }
                }
            } else {
                if (randomDate.getDayOfWeek().name().equals("SATURDAY") || randomDate.getDayOfWeek().name().equals("SUNDAY")) {
                    localDate = randomDate;
                }
            }
        }
        if(localDate==null) {
            localDate = getRandomDateForSkippedRepitition(repetitions, publicHolidayList, boundaryStartDate, boundaryEndDate, isWeekEnd);
        }
        return localDate;

    }

    /*
    Add StartBoundaryDate and EndBoundaryDate  to a RandomDate.
     */
    private List<Map<String, LocalDate>> addBoundaryDates(List<LocalDate> initialRandomDates, LocalDate boundaryStartDate, LocalDate boundaryEndDate) {
        List<Map<String, LocalDate>> randomDatesList = new ArrayList<>();
        for (LocalDate randomDate : initialRandomDates) {
            Map<String, LocalDate> map = new HashMap<>();
            map.put("randomDate", randomDate);
            map.put("taskStartBoundary", boundaryStartDate);
            map.put("taskEndBoundary", boundaryEndDate);
            randomDatesList.add(map);
        }
        return randomDatesList;
    }

    public List<Map<String, LocalDate>> getRandomDatesForDailyPattern(TaskDemand taskDemand) {
        LocalDate taskDemandStartDate = taskDemand.getStartDate().toInstant().atZone(systemDefault()).toLocalDate();
        LocalDate taskDemandEndDate;
        if(taskDemand.getEndDate()==null && taskDemand.getEndAfterOccurrence()>0) {
            taskDemandEndDate = taskDemandStartDate.plusYears(20);
        } else {
            taskDemandEndDate = taskDemand.getEndDate().toInstant().atZone(systemDefault()).toLocalDate();
        }

        LocalDate boundaryStartDate = taskDemandStartDate;
        List<Map<String, LocalDate>> randomDatesList = new ArrayList<>();

        List<LocalDate> randomDateList = new ArrayList<>();
        int counter = 0;
        outerWhileLoop:
        while (taskDemandStartDate.isBefore(taskDemandEndDate) || taskDemandStartDate.isEqual(taskDemandEndDate)) {

            for(int i =1; i <= taskDemand.getDailyFrequency();){
                if((counter%2==0) && (!taskDemandStartDate.getDayOfWeek().name().equals("SATURDAY") && !taskDemandStartDate.getDayOfWeek().name().equals("SUNDAY"))  ){
                    if(taskDemandStartDate.isBefore(taskDemandEndDate) || taskDemandStartDate.isEqual(taskDemandEndDate)){
                        if(taskDemand.getEndDate()==null && randomDateList.size() == taskDemand.getEndAfterOccurrence()){
                            break outerWhileLoop;
                        } else {
                            randomDateList.add(taskDemandStartDate);
                        }
                    }
                }
                taskDemandStartDate = taskDemandStartDate.plusDays(1);
                if(!taskDemandStartDate.getDayOfWeek().name().equals("SATURDAY") && !taskDemandStartDate.getDayOfWeek().name().equals("SUNDAY")){
                    i++;
                }
            }
            counter++;
        }

        LocalDate boundaryEndDate = boundaryStartDate.plusWeeks(4).minusDays(1);
        boundaryEndDate = (boundaryEndDate.isBefore(taskDemandEndDate)) ? boundaryEndDate : taskDemandEndDate;
        randomDatesList.addAll(addBoundaryDates(randomDateList, boundaryStartDate, boundaryEndDate));

        if(taskDemand.getEndDate()==null && taskDemand.getEndAfterOccurrence()>0){
            LocalDate lastRandomDate = randomDatesList.get(randomDatesList.size()-1).get("randomDate");
            taskDemand.setEndDate(Date.from(lastRandomDate.atStartOfDay(systemDefault()).toInstant()));
            save(taskDemand);
        }
        return randomDatesList;
    }





    public List<Map<String, LocalDate>> getRandomDatesForMonthlyPattern(TaskDemand taskDemand) {

        LocalDate taskDemandStartDate = taskDemand.getStartDate().toInstant().atZone(systemDefault()).toLocalDate();
        LocalDate taskDemandEndDate;
        if(taskDemand.getEndDate()==null && taskDemand.getEndAfterOccurrence()>0) {
            taskDemandEndDate = taskDemandStartDate.plusYears(20);
        } else {
            taskDemandEndDate = taskDemand.getEndDate().toInstant().atZone(systemDefault()).toLocalDate();
        }
        List<Map<String, LocalDate>> randomDatesList = new ArrayList<>();

        MonthlyFrequency monthlyFrequency = taskDemand.getMonthlyFrequency();
        taskDemandStartDate = (taskDemandStartDate.getDayOfWeek().name().equals("MONDAY")) ? taskDemandStartDate : DateUtils.calcNextMonday(taskDemandStartDate);
        boolean maximumOccurenceReached = false;
        if (monthlyFrequency.getWeekdayCount() > 0) {

            LocalDate boundaryStartDate = LocalDate.of(taskDemandStartDate.getYear(), taskDemandStartDate.getMonthValue(), taskDemandStartDate.getDayOfMonth());
            LocalDate boundaryEndDate = boundaryStartDate.plusMonths(monthlyFrequency.getMonthFrequency()-1).with(TemporalAdjusters.lastDayOfMonth());
            List<LocalDate> randomDates = new ArrayList<>();
            while (maximumOccurenceReached==false && (taskDemandStartDate.isBefore(taskDemandEndDate) || taskDemandStartDate.isEqual(taskDemandEndDate))) {
                if (!taskDemandStartDate.getDayOfWeek().name().equals("SATURDAY") && !taskDemandStartDate.getDayOfWeek().name().equals("SUNDAY")) {
                    if((taskDemand.getEndDate()==null) && (randomDatesList.size() == taskDemand.getEndAfterOccurrence() || randomDates.size() == taskDemand.getEndAfterOccurrence())){
                        maximumOccurenceReached = true;
                    } else {
                        randomDates.add(taskDemandStartDate);
                    }
                }
                if (maximumOccurenceReached==false && (randomDates.size() < monthlyFrequency.getWeekdayCount()) && (taskDemandStartDate.isBefore(boundaryEndDate) || taskDemandStartDate.isEqual(boundaryEndDate))) {
                    taskDemandStartDate = taskDemandStartDate.plusDays(1);
                } else {
                    boundaryEndDate = (boundaryEndDate.isBefore(taskDemandEndDate)) ? boundaryEndDate : taskDemandEndDate;
                    randomDatesList.addAll(addBoundaryDates(randomDates, boundaryStartDate, boundaryEndDate));
                    randomDates = new ArrayList<>();
                    boundaryStartDate = boundaryStartDate.plusMonths(monthlyFrequency.getMonthFrequency()).with(TemporalAdjusters.firstDayOfMonth());
                    boundaryEndDate = boundaryStartDate.plusMonths(monthlyFrequency.getMonthFrequency()-1).with(TemporalAdjusters.lastDayOfMonth());
                    taskDemandStartDate = boundaryStartDate;
                }
            }
        } else if (monthlyFrequency.getDayOfWeek() != null && monthlyFrequency.getWeekOfMonth() == null) {
            LocalDate boundaryStartDate = LocalDate.of(taskDemandStartDate.getYear(), taskDemandStartDate.getMonthValue(), taskDemandStartDate.getDayOfMonth());
            LocalDate boundaryEndDate = boundaryStartDate.plusMonths(monthlyFrequency.getMonthFrequency());

            DayOfWeek dayOfWeek = DayOfWeek.valueOf(monthlyFrequency.getDayOfWeek().toString());
            taskDemandStartDate = taskDemandStartDate.with(TemporalAdjusters.nextOrSame(dayOfWeek));

            List<LocalDate> randomDates = new ArrayList<>();
            while (maximumOccurenceReached==false && (taskDemandStartDate.isBefore(taskDemandEndDate) || taskDemandStartDate.isEqual(taskDemandEndDate) ) ){

                if (taskDemandStartDate.isBefore(boundaryEndDate) && maximumOccurenceReached == false) {

                    if((taskDemand.getEndDate()==null) && (randomDatesList.size() + randomDates.size() >= taskDemand.getEndAfterOccurrence()) ){
                        maximumOccurenceReached = true;
                        boundaryEndDate = (boundaryEndDate.isBefore(taskDemandEndDate)) ? boundaryEndDate : taskDemandEndDate;
                        randomDatesList.addAll(addBoundaryDates(randomDates, boundaryStartDate, boundaryEndDate));
                    } else {
                        randomDates.add(taskDemandStartDate);
                        taskDemandStartDate = taskDemandStartDate.plusDays(7);
                    }
                } else {
                    boundaryEndDate = (boundaryEndDate.isBefore(taskDemandEndDate)) ? boundaryEndDate : taskDemandEndDate;
                    randomDatesList.addAll(addBoundaryDates(randomDates, boundaryStartDate, boundaryEndDate));
                    randomDates = new ArrayList<>();
                    boundaryStartDate = boundaryStartDate.plusMonths(monthlyFrequency.getMonthFrequency());
                    boundaryEndDate = boundaryEndDate.plusMonths(monthlyFrequency.getMonthFrequency());
                    taskDemandStartDate = boundaryStartDate.with(TemporalAdjusters.nextOrSame(dayOfWeek));
                }
            }
        } else if (monthlyFrequency.getDayOfWeek() != null && monthlyFrequency.getWeekOfMonth() != null) {
            int weekOfMonthAsInt = getWeekOfMonthAsInt(monthlyFrequency.getWeekOfMonth().toString());
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(monthlyFrequency.getDayOfWeek().toString());

            List<LocalDate> randomDates = new ArrayList<>();
            taskDemandStartDate = DateUtils.getDateOfWeekInCurrentOrNextMonth(taskDemandStartDate, weekOfMonthAsInt, dayOfWeek);
            LocalDate boundaryStartDate = taskDemandStartDate.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate boundaryEndDate = boundaryStartDate.plusMonths(monthlyFrequency.getMonthFrequency()-1).with(TemporalAdjusters.lastDayOfMonth());

            while (maximumOccurenceReached==false && (taskDemandStartDate.isBefore(taskDemandEndDate) || taskDemandStartDate.isEqual(taskDemandEndDate))) {

                if((taskDemand.getEndDate()==null) && (randomDatesList.size() >= taskDemand.getEndAfterOccurrence() || randomDates.size() >= taskDemand.getEndAfterOccurrence())){
                    maximumOccurenceReached = true;
                } else {
                    randomDates.add(taskDemandStartDate);
                    boundaryEndDate = (boundaryEndDate.isBefore(taskDemandEndDate)) ? boundaryEndDate : taskDemandEndDate;
                    randomDatesList.addAll(addBoundaryDates(randomDates, boundaryStartDate, boundaryEndDate));

                    randomDates = new ArrayList<>();
                    boundaryStartDate = boundaryStartDate.plusMonths(monthlyFrequency.getMonthFrequency());
                    boundaryEndDate = boundaryStartDate.plusMonths(monthlyFrequency.getMonthFrequency()-1).with(TemporalAdjusters.lastDayOfMonth());
                    taskDemandStartDate = taskDemandStartDate.plusMonths(monthlyFrequency.getMonthFrequency());
                    taskDemandStartDate = DateUtils.getDateOfWeekInMonth(taskDemandStartDate, weekOfMonthAsInt, dayOfWeek);
                }

            }

        }
        logger.debug("getRandomDatesForMonthlyPattern " + randomDatesList);
        if(taskDemand.getEndDate()==null && taskDemand.getEndAfterOccurrence()>0){
            LocalDate lastRandomDate = randomDatesList.get(randomDatesList.size()-1).get("randomDate");
            taskDemand.setEndDate(Date.from(lastRandomDate.atStartOfDay(systemDefault()).toInstant()));
            save(taskDemand);
        }
        return randomDatesList;
    }

    int getWeekOfMonthAsInt(String weekOfMonth) {
        int weekOfMonthInt = 0;
        switch (weekOfMonth) {
            case "FIRST": {
                weekOfMonthInt = 1;
                break;
            }
            case "SECOND": {
                weekOfMonthInt = 2;
                break;
            }
            case "THIRD": {
                weekOfMonthInt = 3;
                break;
            }
            case "FOURTH": {
                weekOfMonthInt = 4;
                break;
            }
        }
        return weekOfMonthInt;
    }

    public int countRandomDatesForDailyPattern(TaskDemand taskDemand) {
        LocalDate taskDemandStartDate = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate taskDemandEndDate = taskDemandStartDate.plusDays(27);


        LocalDate boundaryStartDate = taskDemandStartDate;
        List<Map<String, LocalDate>> randomDatesList = new ArrayList<>();

        List<LocalDate> randomDateList = new ArrayList<>();
        int counter = 0;
        int taskCount = 0;
        outerWhileLoop:
        while (taskDemandStartDate.isBefore(taskDemandEndDate) || taskDemandStartDate.isEqual(taskDemandEndDate)) {

            for(int i =1; i <= taskDemand.getDailyFrequency();){
                if((counter%2==0) && (!taskDemandStartDate.getDayOfWeek().name().equals("SATURDAY") && !taskDemandStartDate.getDayOfWeek().name().equals("SUNDAY"))  ){
                    if(taskDemandStartDate.isBefore(taskDemandEndDate) || taskDemandStartDate.isEqual(taskDemandEndDate)){
                        if(taskDemand.getEndDate()==null && randomDateList.size() == taskDemand.getEndAfterOccurrence()){
                            break outerWhileLoop;
                        } else {
                            taskCount += 1;
                        }
                    }
                }
                taskDemandStartDate = taskDemandStartDate.plusDays(1);
                if(!taskDemandStartDate.getDayOfWeek().name().equals("SATURDAY") && !taskDemandStartDate.getDayOfWeek().name().equals("SUNDAY")){
                    i++;
                }
            }
            counter++;
        }


        return taskCount;
    }

}

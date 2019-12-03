package com.kairos.planner.vrp.taskplanning.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.planner.vrp.taskplanning.solver.VrpTaskPlanningSolver;
import com.kairos.planner.vrp.taskplanning.util.VrpPlanningUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@PlanningEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task extends TaskOrShift{
    private static Logger log= LoggerFactory.getLogger(VrpTaskPlanningSolver.class);
    //TODO consider break in  sub tasks or dont consider merged tasks at all
    private String id;
    private long installationNo;
    private Double latitude;
    private Double longitude;
    private Set<String> skills;
    private int duration;
    private String streetName;
    private int houseNo;
    private String block;
    private int floorNo;
    private int post;
    private String city;
    @PlanningVariable(valueRangeProviderRefs = {
            "tasks","shifts" }, graphType = PlanningVariableGraphType.CHAINED)
    private TaskOrShift prevTaskOrShift;
    @CustomShadowVariable(sources = @PlanningVariableReference(variableName = "prevTaskOrShift"),variableListenerClass = VrpTaskStartTimeListener.class)
    private LocalDateTime plannedStartTime;


    @AnchorShadowVariable(sourceVariableName = "prevTaskOrShift")
    private Shift shift;
    private LocationsDistanceMatrix locationsDistanceMatrix;
    private LocationsRouteMatrix locationsRouteMatrix;
    private boolean shiftBreak;
    public Task(String id, long installationNo, Double latitude, Double longitude, Set<String> skills, int duration, String streetName, int houseNo, String block, int floorNo, int post, String city,boolean shiftBreak) {
        this.id = id;
        this.installationNo = installationNo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.skills = skills;
        this.duration = duration;
        this.streetName = streetName;
        this.houseNo = houseNo;
        this.block = block;
        this.floorNo = floorNo;
        this.post = post;
        this.city = city;
        this.shiftBreak=shiftBreak;
    }
    public Task(long installationNo,  int duration ,boolean shiftBreak) {
        this(UUID.randomUUID().toString(),installationNo,0d,0d, null,duration,null,0,null,0,0,null,shiftBreak);
    }

    public Task() {
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TaskOrShift getPrevTaskOrShift() {
        return prevTaskOrShift;
    }

    public void setPrevTaskOrShift(TaskOrShift prevTaskOrShift) {
        this.prevTaskOrShift = prevTaskOrShift;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public int getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(int houseNo) {
        this.houseNo = houseNo;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public int getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(int floorNo) {
        this.floorNo = floorNo;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

    

    public long getInstallationNo() {
        return installationNo;
    }

    public void setInstallationNo(long installationNo) {
        this.installationNo = installationNo;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getPlannedStartTime() {
        return plannedStartTime;
    }
    public LocalDateTime getPlannedEndTime() {
        if(plannedStartTime ==null) return null;
        return plannedStartTime.plusMinutes((long)getPlannedDuration());

    }

    public void setPlannedStartTime(LocalDateTime plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }

    public LocationsDistanceMatrix getLocationsDistanceMatrix() {
        return locationsDistanceMatrix;
    }

    public void setLocationsDistanceMatrix(LocationsDistanceMatrix locationsDistanceMatrix) {
        this.locationsDistanceMatrix = locationsDistanceMatrix;
    }

    public int getPlannedDuration(){
        return (int)Math.ceil(shiftBreak?duration:this.getDuration()/(this.getShiftFromAnchor().getEmployee().getEfficiency()/100d));
    }
    //for rules only
    public int getDrivingTimeSeconds(){
        if(prevTaskOrShift ==null){
            throw new IllegalStateException("prevTaskOrShift should not be null if its a prt of move.");
        }
        if(prevTaskOrShift instanceof Shift || shiftBreak) return 0;
        Task prevTask=getPreviousValidTask();
        if(prevTask==null) return 0;
        if(Objects.equals(this.latitude,prevTask.latitude) && Objects.equals(this.longitude,prevTask.longitude)){
            return 1;
        }
        LocationPairDifference lpd=locationsDistanceMatrix.getLocationsDifference(new LocationPair(prevTask.getLatitude(),prevTask.getLongitude(),this.getLatitude(),this.getLongitude()));
        return lpd.getTime();
    }

    public int getDrivingDistance(){
        if(prevTaskOrShift ==null){
            throw new IllegalStateException("prevTaskOrShift should not be null if its a prt of move.");
        }
        if(prevTaskOrShift instanceof Shift || shiftBreak) return 0;
        Task prevTask=getPreviousValidTask();
        if(prevTask==null) return 0;
        LocationPairDifference lpd=locationsDistanceMatrix.getLocationsDifference(new LocationPair(prevTask.getLatitude(),prevTask.getLongitude(),this.getLatitude(),this.getLongitude()));
        return lpd.getDistance();
    }

    /**
     * call it using previous task
     * @param task
     * @return
     */
    public Task getPreviousValidTask() {
            return VrpPlanningUtil.getPreviousValidTask(this);
    }

    public Task getPreviousTask() {
        if(this.getPrevTaskOrShift() instanceof Task){
            return (Task) this.getPrevTaskOrShift();
        }
        return null;
    }

    public int getDrivingTime(){
        return (int)Math.ceil(getDrivingTimeSeconds()/60d);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return new EqualsBuilder()
                .append(installationNo, task.installationNo)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(installationNo)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Task{" +
                +installationNo +
                "-" + duration +(shiftBreak?"(break)":"")+
                '}';
    }

    public Shift getShiftFromAnchor(){
        Task temp=this;
        while(temp.getPrevTaskOrShift() instanceof Task){
            temp= (Task) temp.getPrevTaskOrShift();
        }
        return (Shift) temp.getPrevTaskOrShift();
    }

    public String getLatLongString(){
        return latitude +":"+longitude;
    }
    public boolean isConsecutive(Task task){
        return VrpPlanningUtil.isConsecutive(this, task);
    }
    public boolean isEmployeeEligible(){
        return shift==null || shiftBreak || shift.getEmployee().getSkills().containsAll(this.skills);
    }
    public boolean hasSameLocation(Task task){
        return VrpPlanningUtil.hasSameLocation(this,task);
    }
    public boolean hasSameChain(Task task){
        return VrpPlanningUtil.hasSameChain(this,task);
    }
    public boolean hasSameSkillset(Task task){
        return VrpPlanningUtil.hasSameSkillset(this,task);
    }
    public int getMissingSkills(){
            return VrpPlanningUtil.getMissingSkills(this,this.getShift().getEmployee());
    }

    public boolean isShiftBreak() {
        return shiftBreak;
    }

    public void setShiftBreak(boolean shiftBreak) {
        this.shiftBreak = shiftBreak;
    }
    public boolean isBreakInWindow(){

        if(plannedStartTime==null) return true;
        int mins=plannedStartTime.get(ChronoField.MINUTE_OF_DAY);
        //in between 1100 to 1330
        return mins >=660 && mins<=750;
    }
    //0 means 1st task
    public int getOrder(){
        int i=0;
        Task temp=this;
        while(temp.prevTaskOrShift instanceof Task){
            temp= (Task) temp.prevTaskOrShift;
            i++;
        }
        return  i;
    }

    public String getFullStreetName(){
        return city+streetName;
    }
    public boolean areHouseNumberSameLane(Task task){
        return this.houseNo %2 ==task.getHouseNo()%2;
    }

    public LocationsRouteMatrix getLocationsRouteMatrix() {
        return locationsRouteMatrix;
    }

    public void setLocationsRouteMatrix(LocationsRouteMatrix locationsRouteMatrix) {
        this.locationsRouteMatrix = locationsRouteMatrix;
    }
    public boolean isOnOneWayStreet(){
        /*if("5b18f9b4016fe33f761ebd92".equals(id)){
            log.info("itssz");
        }
        if(installationNo==1002074l){
            log.info("itssz");
        }
        if(latitude==56.46226953d){
            log.info("itsz");
        }*/
        return streetName.equals("Adelgade")&&city.equals("Hobro");
    }
    public boolean isTaskLocationOnRightSide(Task prevTask){
        /*assert !prevTask.isShiftBreak();

        if("5b18f9b4016fe33f761ebd92".equals(id)){
            log.info("itssz");
        }
        if(installationNo==1002074l){
            log.info("itssz");
        }
        if(latitude==56.46226953d){
            log.info("itsz");
        }*/
        LocationPair locationPair = new LocationPair(prevTask.getLatitude(), prevTask.getLongitude(), this.getLatitude(), this.getLongitude());
        /*if(locationPair.equals(new LocationPair(56.46219053d,10.03474252d,56.46226953d,10.03495581d))){
            log.info("its"+locationsRouteMatrix.checkIfRightSideArrival(locationPair));
        }*/
        return locationsRouteMatrix.checkIfRightSideArrival(locationPair);
    }
}

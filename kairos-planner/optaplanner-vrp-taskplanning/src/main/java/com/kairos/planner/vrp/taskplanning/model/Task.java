package com.kairos.planner.vrp.taskplanning.model;

import com.kairos.planner.vrp.taskplanning.solver.VrpTaskPlanningSolver;
import com.kairos.planner.vrp.taskplanning.util.VrpPlanningUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@PlanningEntity
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
    private boolean shiftBreak;
    public Task(String id, int installationNo, Double latitude, Double longitude, Set<String> skills, int duration, String streetName, int houseNo, String block, int floorNo, int post, String city) {
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

    public Task() {
    }

    public Task(int installationNo, Double latitude, Double longitude) {
        this.id= UUID.randomUUID().toString();
        this.installationNo = installationNo;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public double getPlannedDuration(){
        return this.getDuration()/(this.getShiftFromAnchor().getEmployee().getEfficiency()/100d);
    }
    //for rules only
    public int getDrivingTimeSeconds(){
        /*if(!false){
            return 0;
        }*/
        if(prevTaskOrShift ==null){
            throw new IllegalStateException("prevTaskOrShift should not be null if its a prt of move.");
        }
        if(prevTaskOrShift instanceof Shift) return 0;
        Task prevTask=(Task)prevTaskOrShift;
        LocationPairDifference lpd=locationsDistanceMatrix.getLocationsDifference(new LocationPair(prevTask.getLatitude(),prevTask.getLongitude(),this.getLatitude(),this.getLongitude()));
        return lpd.getTime();
    }
    public int getDrivingTime(){
        int mins=(int)Math.ceil(getDrivingTimeSeconds()/60d);
        return mins;
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
                "-" + duration +
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
        return shift==null || shift.getEmployee().getSkills().containsAll(this.skills);
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

}

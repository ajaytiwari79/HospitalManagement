package com.kairos.wrapper.task_demand;

import com.kairos.persistence.model.task.TaskAddress;
import com.kairos.dto.user.client.Client;
import com.kairos.dto.user.country.basic_details.CountryHolidayCalender;

import java.util.List;
import java.util.Map;
public class TaskDemandVisitWrapper {

        //required parameters
        private Client citizen;
        private List<Long> forbiddenStaff;
        private List<Long> preferredStaff;
        private TaskAddress taskAddress;
        //optional parameters
        private  Long countryId;
        private Long staffId;
        private Map<String, Object> timeSlotMap;
        private  List<Long> publicHolidayList;
        private Map<String, String> flsCredentials;
        private List<CountryHolidayCalender> countryHolidayCalenderList;



    public TaskDemandVisitWrapper() {
        //default constructor
    }

    private TaskDemandVisitWrapper(TaskDemandVisitWrapperBuilder builder) {
            this.citizen=builder.citizen;
            this.forbiddenStaff=builder.forbiddenStaff;
            this.preferredStaff=builder.preferredStaff;
            this.taskAddress=builder.taskAddress;
            this.countryId=builder.countryId;
            this.staffId=builder.staffId;
            this.timeSlotMap=builder.timeSlotMap;
            this.publicHolidayList=builder.publicHolidayList;
            this.flsCredentials=builder.flsCredentials;
            this.countryHolidayCalenderList=builder.countryHolidayCalenderList;
        }

        //Builder Class
        public static class TaskDemandVisitWrapperBuilder {
            //required parameters
            private Client citizen;
            private List<Long> forbiddenStaff;
            private List<Long> preferredStaff;
            private TaskAddress taskAddress;
            //optional parameters
            private  Long countryId;
            private Long staffId;
            private Map<String, Object> timeSlotMap;
            private  List<Long> publicHolidayList;
            private Map<String, String> flsCredentials;
            private List<CountryHolidayCalender> countryHolidayCalenderList;


            public TaskDemandVisitWrapperBuilder(Client citizen, List<Long> forbiddenStaff,
                                                 List<Long> preferredStaff,TaskAddress taskAddress){
                this.citizen=citizen;
                this.forbiddenStaff=forbiddenStaff;
                this.preferredStaff=preferredStaff;
                this.taskAddress=taskAddress;

            }

            public TaskDemandVisitWrapperBuilder countryId(Long countryId) {
                this.countryId = countryId;
                return this;
            }

            public TaskDemandVisitWrapperBuilder staffId(Long staffId) {
                this.staffId = staffId;
                return this;
            }
            public TaskDemandVisitWrapperBuilder timeSlotMap(Map<String,Object> timeSlotMap) {
                this.timeSlotMap = timeSlotMap;
                return this;
            }
            public TaskDemandVisitWrapperBuilder publicHolidayList(List<Long> publicHolidayList) {
                this.publicHolidayList =publicHolidayList;
                return this;
            }

            public TaskDemandVisitWrapperBuilder flsCredentials(Map<String, String> flsCredentials) {
                this.flsCredentials =flsCredentials;
                return this;
            }

            public TaskDemandVisitWrapperBuilder countryHolidayCalenderList(List<CountryHolidayCalender> countryHolidayCalenderList) {
                this.countryHolidayCalenderList =countryHolidayCalenderList;
                return this;
            }

            public TaskDemandVisitWrapper build(){
                return new TaskDemandVisitWrapper(this);
            }

        }

        public List<Long> getPreferredStaff() {
            return preferredStaff;
        }

        public void setPreferredStaff(List<Long> preferredStaff) {
            this.preferredStaff = preferredStaff;
        }

        public TaskAddress getTaskAddress() {
            return taskAddress;
        }

        public void setTaskAddress(TaskAddress taskAddress) {
            this.taskAddress = taskAddress;
        }

        public List<Long> getForbiddenStaff() {
            return forbiddenStaff;
        }

        public void setForbiddenStaff(List<Long> forbiddenStaff) {
            this.forbiddenStaff = forbiddenStaff;
        }

        public Map<String, Object> getTimeSlotMap() {
            return timeSlotMap;
        }

        public void setTimeSlotMap(Map<String, Object> timeSlotMap) {
            this.timeSlotMap = timeSlotMap;
        }

        public Long getCountryId() {
            return countryId;
        }

        public void setCountryId(Long countryId) {
            this.countryId = countryId;
        }

        public List<Long> getPublicHolidayList() {
            return publicHolidayList;
        }

        public void setPublicHolidayList(List<Long> publicHolidayList) {
            this.publicHolidayList = publicHolidayList;
        }

        public Long getStaffId() {
            return staffId;
        }

        public void setStaffId(Long staffId) {
            this.staffId = staffId;
        }

        public Client getCitizen() {
            return citizen;
        }

        public void setCitizen(Client citizen) {
            this.citizen = citizen;
        }

        public Map<String, String> getFlsCredentials() {
            return flsCredentials;
        }

        public void setFlsCredentials(Map<String, String> flsCredentials) {
            this.flsCredentials = flsCredentials;
        }

        public List<CountryHolidayCalender> getCountryHolidayCalenderList() {
            return countryHolidayCalenderList;
        }

        public void setCountryHolidayCalenderList(List<CountryHolidayCalender> countryHolidayCalenderList) {
            this.countryHolidayCalenderList = countryHolidayCalenderList;
        }
    }



package com.planner.responseDto.citizenDto;

import com.planner.responseDto.commonDto.BaseDTO;

import java.util.List;

public class OptaCitizenDTO extends BaseDTO{

//    private OptaLocationDTO address;
    private String firstName;
    private String lastName;
    private List<Long> preferedStaff;
    private List<Long> forbidenStaff;

  /*  public OptaLocationDTO getAddress() {
        return address;
    }

    public void setAddress(OptaLocationDTO address) {
        this.address = address;
    }
*/
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Long> getPreferedStaff() {
        return preferedStaff;
    }

    public void setPreferedStaff(List<Long> preferedStaff) {
        this.preferedStaff = preferedStaff;
    }

    public List<Long> getForbidenStaff() {
        return forbidenStaff;
    }

    public void setForbidenStaff(List<Long> forbidenStaff) {
        this.forbidenStaff = forbidenStaff;
    }

}

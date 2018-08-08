package com.kairos.persistence.model.client.queryResults;

import com.kairos.enums.Gender;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 25/4/17.
 */
@QueryResult
public class ClientStaffQueryResult {

    private long id;
    private String name;
    private Gender gender;
    private String profilePic;
    private int age;
    private Map<String,Object> localAreaTag;
    private String address;

    public Map<String, Object> getLocalAreaTag() {
        return localAreaTag;
    }

    public void setLocalAreaTag(Map<String, Object> localAreaTag) {
        this.localAreaTag = localAreaTag;
    }
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getProfilePic() {

        return profilePic;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Gender getGender() {

        return gender;
    }

    public void setId(long id) {
        this.id = id;

    }

    public void setName(String name) {
        this.name = name;
    }


    public long getId() {

        return id;
    }

    public String getName() {
        return name;
    }

    public void setStaff(List<Map<String, Object>> staff) {
        this.staff = staff;
    }

    public List<Map<String, Object>> getStaff() {

        return staff;
    }

    List<Map<String,Object>> staff;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "ClientStaffQueryResult{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", staff=" + staff +
                '}';
    }
}

package com.kairos.persistence.model.user.client;
import com.kairos.persistence.model.enums.Gender;
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

    @Override
    public String toString() {
        return "ClientStaffQueryResult{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", staff=" + staff +
                '}';
    }
}

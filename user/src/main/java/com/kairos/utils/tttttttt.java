package com.kairos.utils;

import com.kairos.dto.user.country.filter.FilterDetailDTO;
import com.kairos.enums.Gender;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * CreatedBy vipulpandey on 28/9/18
 **/
 enum Tes{
    MALE("Male"), FEMALE("Female");
    public String value;

    Tes(String value) {
        this.value = value;
    }

    public static Tes getByValue(String value) {
        for (Tes gender : Tes.values()) {
            if (gender.value.equals(value)) {
                return gender;
            }
        }
        return null;
    }

}
public class tttttttt {
    Tes tes;
    int age;

    public Tes getTes() {
        return tes;
    }

    public void setTes(Tes tes) {
        this.tes = tes;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static void main(String[] args) {
        tttttttt   c= new tttttttt();
        c.setTes(AAA'');
        c.setAge(5);
        System.out.println(c);

    }

    @Override
    public String toString() {
        return "tttttttt{" +
                "tes=" + tes +
                ", age=" + age +
                '}';
    }
}

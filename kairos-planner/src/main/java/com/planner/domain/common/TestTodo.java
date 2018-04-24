package com.planner.domain.common;

//import org.springframework.data.cassandra.core.mapping.PrimaryKey;
////import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigInteger;

////@Table
public class TestTodo {

    //@PrimaryKey
    private BigInteger id;

    private String name;
    private int age;
    private String lastName;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}

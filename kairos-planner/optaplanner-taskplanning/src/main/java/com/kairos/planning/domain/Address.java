package com.kairos.planning.domain;

public class Address {
	private String streetAddress1;
	private String streetAddress2;
	private String country;
	private String houseNo;
	private int zip;
	private String city;
	
	public Address(String streetAddress1,String streetAddress2,String country,String houseNo,int zip,String city){
		this.streetAddress1 = streetAddress1;
		this.streetAddress2 = streetAddress2;
		this.country = country;
		this.houseNo = houseNo;
		this.zip = zip;
		this.city = city;
	}
	
	public String getStreetAddress1() {
		return streetAddress1;
	}
	public void setStreetAddress1(String streetAddress1) {
		streetAddress1 = streetAddress1;
	}
	public String getStreetAddress2() {
		return streetAddress2;
	}
	public void setStreetAddress2(String streetAddress2) {
		streetAddress2 = streetAddress2;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getHouseNo() {
		return houseNo;
	}
	public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
	}
	public int getZip() {
		return zip;
	}
	public void setZip(int zip) {
		this.zip = zip;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	
}
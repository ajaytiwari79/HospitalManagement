
package de.tourenserver;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FunctionCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Mandator" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Agent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExtID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="VTID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CustomerExtID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Name2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ContactPerson" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Phone1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Phone2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CallInfo1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CallInfo2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Userfield1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Userfield2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ZIP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="City" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="District" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Street" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="x" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="y" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="RegionID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AreaOfExpertiseID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TeamID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TaskTypeID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Servicetype" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Priority" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="FixedFieldManagerID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ForbiddenFieldManagerID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PreferredFieldmanagerID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PreferredFieldmanagerID2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AllowedFieldmanagerIDs" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExternalProcessing" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Skills" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Units" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="DateFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="DateTo" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="TimeFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="TimeTo" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="OpeningHours" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Weekday" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TimeAttribute" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Duration" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Preparationtime" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="PostProcessing" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="SetupTime" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Addresstype" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="SequenceGroup" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ExtraPenalty" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="DelayPenalty" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="FixedDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="MaxDetour" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Reservationtime" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ShowCall" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SecondaryVisit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EquipmentNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EquipmentName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Relation_ExtID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Relation_type" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="StandbyType" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Interruptible" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="MaxCost" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="MaxKm" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "functionCode",
    "mandator",
    "agent",
    "extID",
    "vtid",
    "customerExtID",
    "name",
    "name2",
    "title",
    "contactPerson",
    "phone1",
    "phone2",
    "email",
    "callInfo1",
    "callInfo2",
    "userfield1",
    "userfield2",
    "country",
    "zip",
    "city",
    "district",
    "street",
    "hNr",
    "x",
    "y",
    "regionID",
    "areaOfExpertiseID",
    "teamID",
    "taskTypeID",
    "servicetype",
    "priority",
    "fixedFieldManagerID",
    "forbiddenFieldManagerID",
    "preferredFieldmanagerID",
    "preferredFieldmanagerID2",
    "allowedFieldmanagerIDs",
    "externalProcessing",
    "skills",
    "units",
    "dateFrom",
    "dateTo",
    "timeFrom",
    "timeTo",
    "openingHours",
    "weekday",
    "timeAttribute",
    "duration",
    "preparationtime",
    "postProcessing",
    "setupTime",
    "addresstype",
    "sequenceGroup",
    "extraPenalty",
    "delayPenalty",
    "fixedDate",
    "state",
    "maxDetour",
    "reservationtime",
    "showCall",
    "secondaryVisit",
    "equipmentNr",
    "equipmentName",
    "relationExtID",
    "relationType",
    "standbyType",
    "interruptible",
    "maxCost",
    "maxKm"
})
@XmlRootElement(name = "Call")
public class Call {

    @XmlElement(name = "FunctionCode")
    protected int functionCode;
    @XmlElement(name = "Mandator")
    protected Integer mandator;
    @XmlElement(name = "Agent")
    protected String agent;
    @XmlElement(name = "ExtID", required = true)
    protected String extID;
    @XmlElement(name = "VTID")
    protected int vtid;
    @XmlElement(name = "CustomerExtID")
    protected String customerExtID;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "Name2")
    protected String name2;
    @XmlElement(name = "Title")
    protected String title;
    @XmlElement(name = "ContactPerson")
    protected String contactPerson;
    @XmlElement(name = "Phone1")
    protected String phone1;
    @XmlElement(name = "Phone2")
    protected String phone2;
    @XmlElement(name = "Email")
    protected String email;
    @XmlElement(name = "CallInfo1")
    protected String callInfo1;
    @XmlElement(name = "CallInfo2")
    protected String callInfo2;
    @XmlElement(name = "Userfield1")
    protected String userfield1;
    @XmlElement(name = "Userfield2")
    protected String userfield2;
    @XmlElement(name = "Country")
    protected String country;
    @XmlElement(name = "ZIP")
    protected String zip;
    @XmlElement(name = "City")
    protected String city;
    @XmlElement(name = "District")
    protected String district;
    @XmlElement(name = "Street")
    protected String street;
    @XmlElement(name = "HNr")
    protected String hNr;
    protected Integer x;
    protected Integer y;
    @XmlElement(name = "RegionID")
    protected String regionID;
    @XmlElement(name = "AreaOfExpertiseID")
    protected String areaOfExpertiseID;
    @XmlElement(name = "TeamID")
    protected String teamID;
    @XmlElement(name = "TaskTypeID")
    protected String taskTypeID;
    @XmlElement(name = "Servicetype")
    protected Integer servicetype;
    @XmlElement(name = "Priority")
    protected Integer priority;
    @XmlElement(name = "FixedFieldManagerID")
    protected String fixedFieldManagerID;
    @XmlElement(name = "ForbiddenFieldManagerID")
    protected String forbiddenFieldManagerID;
    @XmlElement(name = "PreferredFieldmanagerID")
    protected String preferredFieldmanagerID;
    @XmlElement(name = "PreferredFieldmanagerID2")
    protected String preferredFieldmanagerID2;
    @XmlElement(name = "AllowedFieldmanagerIDs")
    protected String allowedFieldmanagerIDs;
    @XmlElement(name = "ExternalProcessing")
    protected Integer externalProcessing;
    @XmlElement(name = "Skills")
    protected String skills;
    @XmlElement(name = "Units")
    protected Integer units;
    @XmlElement(name = "DateFrom")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateFrom;
    @XmlElement(name = "DateTo")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateTo;
    @XmlElement(name = "TimeFrom")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timeFrom;
    @XmlElement(name = "TimeTo")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timeTo;
    @XmlElement(name = "OpeningHours")
    protected String openingHours;
    @XmlElement(name = "Weekday")
    protected String weekday;
    @XmlElement(name = "TimeAttribute")
    protected String timeAttribute;
    @XmlElement(name = "Duration")
    protected Integer duration;
    @XmlElement(name = "Preparationtime")
    protected Integer preparationtime;
    @XmlElement(name = "PostProcessing")
    protected Integer postProcessing;
    @XmlElement(name = "SetupTime")
    protected Integer setupTime;
    @XmlElement(name = "Addresstype")
    protected Integer addresstype;
    @XmlElement(name = "SequenceGroup")
    protected Integer sequenceGroup;
    @XmlElement(name = "ExtraPenalty")
    protected Integer extraPenalty;
    @XmlElement(name = "DelayPenalty")
    protected Float delayPenalty;
    @XmlElement(name = "FixedDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fixedDate;
    @XmlElement(name = "State")
    protected Integer state;
    @XmlElement(name = "MaxDetour")
    protected Integer maxDetour;
    @XmlElement(name = "Reservationtime")
    protected Integer reservationtime;
    @XmlElement(name = "ShowCall")
    protected String showCall;
    @XmlElement(name = "SecondaryVisit")
    protected String secondaryVisit;
    @XmlElement(name = "EquipmentNr")
    protected String equipmentNr;
    @XmlElement(name = "EquipmentName")
    protected String equipmentName;
    @XmlElement(name = "Relation_ExtID", required = true)
    protected String relationExtID;
    @XmlElement(name = "Relation_type")
    protected int relationType;
    @XmlElement(name = "StandbyType")
    protected Integer standbyType;
    @XmlElement(name = "Interruptible")
    protected Integer interruptible;
    @XmlElement(name = "MaxCost")
    protected Integer maxCost;
    @XmlElement(name = "MaxKm")
    protected Integer maxKm;

    /**
     * Gets the value of the functionCode property.
     * 
     */
    public int getFunctionCode() {
        return functionCode;
    }

    /**
     * Sets the value of the functionCode property.
     * 
     */
    public void setFunctionCode(int value) {
        this.functionCode = value;
    }

    /**
     * Gets the value of the mandator property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMandator() {
        return mandator;
    }

    /**
     * Sets the value of the mandator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMandator(Integer value) {
        this.mandator = value;
    }

    /**
     * Gets the value of the agent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgent() {
        return agent;
    }

    /**
     * Sets the value of the agent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgent(String value) {
        this.agent = value;
    }

    /**
     * Gets the value of the extID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtID() {
        return extID;
    }

    /**
     * Sets the value of the extID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtID(String value) {
        this.extID = value;
    }

    /**
     * Gets the value of the vtid property.
     * 
     */
    public int getVTID() {
        return vtid;
    }

    /**
     * Sets the value of the vtid property.
     * 
     */
    public void setVTID(int value) {
        this.vtid = value;
    }

    /**
     * Gets the value of the customerExtID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerExtID() {
        return customerExtID;
    }

    /**
     * Sets the value of the customerExtID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerExtID(String value) {
        this.customerExtID = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the name2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName2() {
        return name2;
    }

    /**
     * Sets the value of the name2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName2(String value) {
        this.name2 = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the contactPerson property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactPerson() {
        return contactPerson;
    }

    /**
     * Sets the value of the contactPerson property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactPerson(String value) {
        this.contactPerson = value;
    }

    /**
     * Gets the value of the phone1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhone1() {
        return phone1;
    }

    /**
     * Sets the value of the phone1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhone1(String value) {
        this.phone1 = value;
    }

    /**
     * Gets the value of the phone2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhone2() {
        return phone2;
    }

    /**
     * Sets the value of the phone2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhone2(String value) {
        this.phone2 = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the callInfo1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCallInfo1() {
        return callInfo1;
    }

    /**
     * Sets the value of the callInfo1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCallInfo1(String value) {
        this.callInfo1 = value;
    }

    /**
     * Gets the value of the callInfo2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCallInfo2() {
        return callInfo2;
    }

    /**
     * Sets the value of the callInfo2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCallInfo2(String value) {
        this.callInfo2 = value;
    }

    /**
     * Gets the value of the userfield1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserfield1() {
        return userfield1;
    }

    /**
     * Sets the value of the userfield1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserfield1(String value) {
        this.userfield1 = value;
    }

    /**
     * Gets the value of the userfield2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserfield2() {
        return userfield2;
    }

    /**
     * Sets the value of the userfield2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserfield2(String value) {
        this.userfield2 = value;
    }

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountry(String value) {
        this.country = value;
    }

    /**
     * Gets the value of the zip property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZIP() {
        return zip;
    }

    /**
     * Sets the value of the zip property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZIP(String value) {
        this.zip = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the district property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDistrict() {
        return district;
    }

    /**
     * Sets the value of the district property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDistrict(String value) {
        this.district = value;
    }

    /**
     * Gets the value of the street property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the value of the street property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreet(String value) {
        this.street = value;
    }

    /**
     * Gets the value of the hNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHNr() {
        return hNr;
    }

    /**
     * Sets the value of the hNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHNr(String value) {
        this.hNr = value;
    }

    /**
     * Gets the value of the x property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setX(Integer value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setY(Integer value) {
        this.y = value;
    }

    /**
     * Gets the value of the regionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegionID() {
        return regionID;
    }

    /**
     * Sets the value of the regionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegionID(String value) {
        this.regionID = value;
    }

    /**
     * Gets the value of the areaOfExpertiseID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAreaOfExpertiseID() {
        return areaOfExpertiseID;
    }

    /**
     * Sets the value of the areaOfExpertiseID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAreaOfExpertiseID(String value) {
        this.areaOfExpertiseID = value;
    }

    /**
     * Gets the value of the teamID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTeamID() {
        return teamID;
    }

    /**
     * Sets the value of the teamID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTeamID(String value) {
        this.teamID = value;
    }

    /**
     * Gets the value of the taskTypeID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaskTypeID() {
        return taskTypeID;
    }

    /**
     * Sets the value of the taskTypeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaskTypeID(String value) {
        this.taskTypeID = value;
    }

    /**
     * Gets the value of the servicetype property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getServicetype() {
        return servicetype;
    }

    /**
     * Sets the value of the servicetype property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setServicetype(Integer value) {
        this.servicetype = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPriority(Integer value) {
        this.priority = value;
    }

    /**
     * Gets the value of the fixedFieldManagerID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFixedFieldManagerID() {
        return fixedFieldManagerID;
    }

    /**
     * Sets the value of the fixedFieldManagerID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFixedFieldManagerID(String value) {
        this.fixedFieldManagerID = value;
    }

    /**
     * Gets the value of the forbiddenFieldManagerID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForbiddenFieldManagerID() {
        return forbiddenFieldManagerID;
    }

    /**
     * Sets the value of the forbiddenFieldManagerID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForbiddenFieldManagerID(String value) {
        this.forbiddenFieldManagerID = value;
    }

    /**
     * Gets the value of the preferredFieldmanagerID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreferredFieldmanagerID() {
        return preferredFieldmanagerID;
    }

    /**
     * Sets the value of the preferredFieldmanagerID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreferredFieldmanagerID(String value) {
        this.preferredFieldmanagerID = value;
    }

    /**
     * Gets the value of the preferredFieldmanagerID2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreferredFieldmanagerID2() {
        return preferredFieldmanagerID2;
    }

    /**
     * Sets the value of the preferredFieldmanagerID2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreferredFieldmanagerID2(String value) {
        this.preferredFieldmanagerID2 = value;
    }

    /**
     * Gets the value of the allowedFieldmanagerIDs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAllowedFieldmanagerIDs() {
        return allowedFieldmanagerIDs;
    }

    /**
     * Sets the value of the allowedFieldmanagerIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllowedFieldmanagerIDs(String value) {
        this.allowedFieldmanagerIDs = value;
    }

    /**
     * Gets the value of the externalProcessing property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getExternalProcessing() {
        return externalProcessing;
    }

    /**
     * Sets the value of the externalProcessing property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setExternalProcessing(Integer value) {
        this.externalProcessing = value;
    }

    /**
     * Gets the value of the skills property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSkills() {
        return skills;
    }

    /**
     * Sets the value of the skills property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSkills(String value) {
        this.skills = value;
    }

    /**
     * Gets the value of the units property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUnits() {
        return units;
    }

    /**
     * Sets the value of the units property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUnits(Integer value) {
        this.units = value;
    }

    /**
     * Gets the value of the dateFrom property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateFrom() {
        return dateFrom;
    }

    /**
     * Sets the value of the dateFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateFrom(XMLGregorianCalendar value) {
        this.dateFrom = value;
    }

    /**
     * Gets the value of the dateTo property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateTo() {
        return dateTo;
    }

    /**
     * Sets the value of the dateTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateTo(XMLGregorianCalendar value) {
        this.dateTo = value;
    }

    /**
     * Gets the value of the timeFrom property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimeFrom() {
        return timeFrom;
    }

    /**
     * Sets the value of the timeFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimeFrom(XMLGregorianCalendar value) {
        this.timeFrom = value;
    }

    /**
     * Gets the value of the timeTo property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimeTo() {
        return timeTo;
    }

    /**
     * Sets the value of the timeTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimeTo(XMLGregorianCalendar value) {
        this.timeTo = value;
    }

    /**
     * Gets the value of the openingHours property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpeningHours() {
        return openingHours;
    }

    /**
     * Sets the value of the openingHours property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpeningHours(String value) {
        this.openingHours = value;
    }

    /**
     * Gets the value of the weekday property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWeekday() {
        return weekday;
    }

    /**
     * Sets the value of the weekday property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWeekday(String value) {
        this.weekday = value;
    }

    /**
     * Gets the value of the timeAttribute property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeAttribute() {
        return timeAttribute;
    }

    /**
     * Sets the value of the timeAttribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeAttribute(String value) {
        this.timeAttribute = value;
    }

    /**
     * Gets the value of the duration property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDuration(Integer value) {
        this.duration = value;
    }

    /**
     * Gets the value of the preparationtime property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPreparationtime() {
        return preparationtime;
    }

    /**
     * Sets the value of the preparationtime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPreparationtime(Integer value) {
        this.preparationtime = value;
    }

    /**
     * Gets the value of the postProcessing property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPostProcessing() {
        return postProcessing;
    }

    /**
     * Sets the value of the postProcessing property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPostProcessing(Integer value) {
        this.postProcessing = value;
    }

    /**
     * Gets the value of the setupTime property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSetupTime() {
        return setupTime;
    }

    /**
     * Sets the value of the setupTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSetupTime(Integer value) {
        this.setupTime = value;
    }

    /**
     * Gets the value of the addresstype property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAddresstype() {
        return addresstype;
    }

    /**
     * Sets the value of the addresstype property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAddresstype(Integer value) {
        this.addresstype = value;
    }

    /**
     * Gets the value of the sequenceGroup property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSequenceGroup() {
        return sequenceGroup;
    }

    /**
     * Sets the value of the sequenceGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSequenceGroup(Integer value) {
        this.sequenceGroup = value;
    }

    /**
     * Gets the value of the extraPenalty property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getExtraPenalty() {
        return extraPenalty;
    }

    /**
     * Sets the value of the extraPenalty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setExtraPenalty(Integer value) {
        this.extraPenalty = value;
    }

    /**
     * Gets the value of the delayPenalty property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getDelayPenalty() {
        return delayPenalty;
    }

    /**
     * Sets the value of the delayPenalty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setDelayPenalty(Float value) {
        this.delayPenalty = value;
    }

    /**
     * Gets the value of the fixedDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFixedDate() {
        return fixedDate;
    }

    /**
     * Sets the value of the fixedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFixedDate(XMLGregorianCalendar value) {
        this.fixedDate = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setState(Integer value) {
        this.state = value;
    }

    /**
     * Gets the value of the maxDetour property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxDetour() {
        return maxDetour;
    }

    /**
     * Sets the value of the maxDetour property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxDetour(Integer value) {
        this.maxDetour = value;
    }

    /**
     * Gets the value of the reservationtime property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getReservationtime() {
        return reservationtime;
    }

    /**
     * Sets the value of the reservationtime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setReservationtime(Integer value) {
        this.reservationtime = value;
    }

    /**
     * Gets the value of the showCall property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShowCall() {
        return showCall;
    }

    /**
     * Sets the value of the showCall property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShowCall(String value) {
        this.showCall = value;
    }

    /**
     * Gets the value of the secondaryVisit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecondaryVisit() {
        return secondaryVisit;
    }

    /**
     * Sets the value of the secondaryVisit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecondaryVisit(String value) {
        this.secondaryVisit = value;
    }

    /**
     * Gets the value of the equipmentNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEquipmentNr() {
        return equipmentNr;
    }

    /**
     * Sets the value of the equipmentNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEquipmentNr(String value) {
        this.equipmentNr = value;
    }

    /**
     * Gets the value of the equipmentName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEquipmentName() {
        return equipmentName;
    }

    /**
     * Sets the value of the equipmentName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEquipmentName(String value) {
        this.equipmentName = value;
    }

    /**
     * Gets the value of the relationExtID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelationExtID() {
        return relationExtID;
    }

    /**
     * Sets the value of the relationExtID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelationExtID(String value) {
        this.relationExtID = value;
    }

    /**
     * Gets the value of the relationType property.
     * 
     */
    public int getRelationType() {
        return relationType;
    }

    /**
     * Sets the value of the relationType property.
     * 
     */
    public void setRelationType(int value) {
        this.relationType = value;
    }

    /**
     * Gets the value of the standbyType property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStandbyType() {
        return standbyType;
    }

    /**
     * Sets the value of the standbyType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStandbyType(Integer value) {
        this.standbyType = value;
    }

    /**
     * Gets the value of the interruptible property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getInterruptible() {
        return interruptible;
    }

    /**
     * Sets the value of the interruptible property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setInterruptible(Integer value) {
        this.interruptible = value;
    }

    /**
     * Gets the value of the maxCost property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxCost() {
        return maxCost;
    }

    /**
     * Sets the value of the maxCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxCost(Integer value) {
        this.maxCost = value;
    }

    /**
     * Gets the value of the maxKm property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxKm() {
        return maxKm;
    }

    /**
     * Sets the value of the maxKm property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxKm(Integer value) {
        this.maxKm = value;
    }

}

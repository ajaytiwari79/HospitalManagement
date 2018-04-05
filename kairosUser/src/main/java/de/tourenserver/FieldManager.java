
package de.tourenserver;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="FMVTID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="FMExtID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Active" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Prename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RegionID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AreaOfExpertiseID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TeamID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SCountry" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SZIP" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SCity" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SDistrict" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SStreet" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SX" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SY" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SNID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ECountry" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EZIP" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ECity" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EDistrict" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EStreet" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EX" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="EY" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ENID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Info" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Mobile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Fax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LSkills" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Capacity" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="SpeedPercent" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="WorkPercent" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Overtime" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="CostDay" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="CostCall" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="CostKm" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="CostHour" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="CostHourOvertime" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
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
    "fmvtid",
    "fmExtID",
    "type",
    "active",
    "prename",
    "name",
    "regionID",
    "areaOfExpertiseID",
    "teamID",
    "sCountry",
    "szip",
    "sCity",
    "sDistrict",
    "sStreet",
    "sx",
    "sy",
    "snid",
    "eCountry",
    "ezip",
    "eCity",
    "eDistrict",
    "eStreet",
    "ex",
    "ey",
    "enid",
    "info",
    "email",
    "phone",
    "mobile",
    "fax",
    "lSkills",
    "capacity",
    "speedPercent",
    "workPercent",
    "overtime",
    "costDay",
    "costCall",
    "costKm",
    "costHour",
    "costHourOvertime"
})
@XmlRootElement(name = "FieldManager")
public class FieldManager {

    @XmlElement(name = "FMVTID")
    protected int fmvtid;
    @XmlElement(name = "FMExtID", required = true)
    protected String fmExtID;
    @XmlElement(name = "Type")
    protected String type;
    @XmlElement(name = "Active")
    protected Boolean active;
    @XmlElement(name = "Prename")
    protected String prename;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "RegionID")
    protected String regionID;
    @XmlElement(name = "AreaOfExpertiseID")
    protected String areaOfExpertiseID;
    @XmlElement(name = "TeamID")
    protected String teamID;
    @XmlElement(name = "SCountry", required = true)
    protected String sCountry;
    @XmlElement(name = "SZIP", required = true)
    protected String szip;
    @XmlElement(name = "SCity", required = true)
    protected String sCity;
    @XmlElement(name = "SDistrict", required = true)
    protected String sDistrict;
    @XmlElement(name = "SStreet", required = true)
    protected String sStreet;
    @XmlElement(name = "SX")
    protected int sx;
    @XmlElement(name = "SY")
    protected int sy;
    @XmlElement(name = "SNID")
    protected int snid;
    @XmlElement(name = "ECountry", required = true)
    protected String eCountry;
    @XmlElement(name = "EZIP", required = true)
    protected String ezip;
    @XmlElement(name = "ECity", required = true)
    protected String eCity;
    @XmlElement(name = "EDistrict", required = true)
    protected String eDistrict;
    @XmlElement(name = "EStreet", required = true)
    protected String eStreet;
    @XmlElement(name = "EX")
    protected int ex;
    @XmlElement(name = "EY")
    protected int ey;
    @XmlElement(name = "ENID")
    protected int enid;
    @XmlElement(name = "Info")
    protected String info;
    @XmlElement(name = "Email")
    protected String email;
    @XmlElement(name = "Phone")
    protected String phone;
    @XmlElement(name = "Mobile")
    protected String mobile;
    @XmlElement(name = "Fax")
    protected String fax;
    @XmlElement(name = "LSkills")
    protected String lSkills;
    @XmlElement(name = "Capacity")
    protected Integer capacity;
    @XmlElement(name = "SpeedPercent")
    protected Integer speedPercent;
    @XmlElement(name = "WorkPercent")
    protected Integer workPercent;
    @XmlElement(name = "Overtime")
    protected Integer overtime;
    @XmlElement(name = "CostDay")
    protected Float costDay;
    @XmlElement(name = "CostCall")
    protected Float costCall;
    @XmlElement(name = "CostKm")
    protected Float costKm;
    @XmlElement(name = "CostHour")
    protected Float costHour;
    @XmlElement(name = "CostHourOvertime")
    protected Float costHourOvertime;

    /**
     * Gets the value of the fmvtid property.
     * 
     */
    public int getFMVTID() {
        return fmvtid;
    }

    /**
     * Sets the value of the fmvtid property.
     * 
     */
    public void setFMVTID(int value) {
        this.fmvtid = value;
    }

    /**
     * Gets the value of the fmExtID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFMExtID() {
        return fmExtID;
    }

    /**
     * Sets the value of the fmExtID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFMExtID(String value) {
        this.fmExtID = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the active property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isActive() {
        return active;
    }

    /**
     * Sets the value of the active property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setActive(Boolean value) {
        this.active = value;
    }

    /**
     * Gets the value of the prename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrename() {
        return prename;
    }

    /**
     * Sets the value of the prename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrename(String value) {
        this.prename = value;
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
     * Gets the value of the sCountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSCountry() {
        return sCountry;
    }

    /**
     * Sets the value of the sCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSCountry(String value) {
        this.sCountry = value;
    }

    /**
     * Gets the value of the szip property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSZIP() {
        return szip;
    }

    /**
     * Sets the value of the szip property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSZIP(String value) {
        this.szip = value;
    }

    /**
     * Gets the value of the sCity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSCity() {
        return sCity;
    }

    /**
     * Sets the value of the sCity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSCity(String value) {
        this.sCity = value;
    }

    /**
     * Gets the value of the sDistrict property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSDistrict() {
        return sDistrict;
    }

    /**
     * Sets the value of the sDistrict property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSDistrict(String value) {
        this.sDistrict = value;
    }

    /**
     * Gets the value of the sStreet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSStreet() {
        return sStreet;
    }

    /**
     * Sets the value of the sStreet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSStreet(String value) {
        this.sStreet = value;
    }

    /**
     * Gets the value of the sx property.
     * 
     */
    public int getSX() {
        return sx;
    }

    /**
     * Sets the value of the sx property.
     * 
     */
    public void setSX(int value) {
        this.sx = value;
    }

    /**
     * Gets the value of the sy property.
     * 
     */
    public int getSY() {
        return sy;
    }

    /**
     * Sets the value of the sy property.
     * 
     */
    public void setSY(int value) {
        this.sy = value;
    }

    /**
     * Gets the value of the snid property.
     * 
     */
    public int getSNID() {
        return snid;
    }

    /**
     * Sets the value of the snid property.
     * 
     */
    public void setSNID(int value) {
        this.snid = value;
    }

    /**
     * Gets the value of the eCountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getECountry() {
        return eCountry;
    }

    /**
     * Sets the value of the eCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setECountry(String value) {
        this.eCountry = value;
    }

    /**
     * Gets the value of the ezip property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEZIP() {
        return ezip;
    }

    /**
     * Sets the value of the ezip property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEZIP(String value) {
        this.ezip = value;
    }

    /**
     * Gets the value of the eCity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getECity() {
        return eCity;
    }

    /**
     * Sets the value of the eCity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setECity(String value) {
        this.eCity = value;
    }

    /**
     * Gets the value of the eDistrict property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEDistrict() {
        return eDistrict;
    }

    /**
     * Sets the value of the eDistrict property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEDistrict(String value) {
        this.eDistrict = value;
    }

    /**
     * Gets the value of the eStreet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEStreet() {
        return eStreet;
    }

    /**
     * Sets the value of the eStreet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEStreet(String value) {
        this.eStreet = value;
    }

    /**
     * Gets the value of the ex property.
     * 
     */
    public int getEX() {
        return ex;
    }

    /**
     * Sets the value of the ex property.
     * 
     */
    public void setEX(int value) {
        this.ex = value;
    }

    /**
     * Gets the value of the ey property.
     * 
     */
    public int getEY() {
        return ey;
    }

    /**
     * Sets the value of the ey property.
     * 
     */
    public void setEY(int value) {
        this.ey = value;
    }

    /**
     * Gets the value of the enid property.
     * 
     */
    public int getENID() {
        return enid;
    }

    /**
     * Sets the value of the enid property.
     * 
     */
    public void setENID(int value) {
        this.enid = value;
    }

    /**
     * Gets the value of the info property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInfo() {
        return info;
    }

    /**
     * Sets the value of the info property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInfo(String value) {
        this.info = value;
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
     * Gets the value of the phone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhone(String value) {
        this.phone = value;
    }

    /**
     * Gets the value of the mobile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * Sets the value of the mobile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMobile(String value) {
        this.mobile = value;
    }

    /**
     * Gets the value of the fax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFax() {
        return fax;
    }

    /**
     * Sets the value of the fax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFax(String value) {
        this.fax = value;
    }

    /**
     * Gets the value of the lSkills property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLSkills() {
        return lSkills;
    }

    /**
     * Sets the value of the lSkills property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLSkills(String value) {
        this.lSkills = value;
    }

    /**
     * Gets the value of the capacity property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * Sets the value of the capacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCapacity(Integer value) {
        this.capacity = value;
    }

    /**
     * Gets the value of the speedPercent property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSpeedPercent() {
        return speedPercent;
    }

    /**
     * Sets the value of the speedPercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSpeedPercent(Integer value) {
        this.speedPercent = value;
    }

    /**
     * Gets the value of the workPercent property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getWorkPercent() {
        return workPercent;
    }

    /**
     * Sets the value of the workPercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setWorkPercent(Integer value) {
        this.workPercent = value;
    }

    /**
     * Gets the value of the overtime property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOvertime() {
        return overtime;
    }

    /**
     * Sets the value of the overtime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOvertime(Integer value) {
        this.overtime = value;
    }

    /**
     * Gets the value of the costDay property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getCostDay() {
        return costDay;
    }

    /**
     * Sets the value of the costDay property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setCostDay(Float value) {
        this.costDay = value;
    }

    /**
     * Gets the value of the costCall property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getCostCall() {
        return costCall;
    }

    /**
     * Sets the value of the costCall property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setCostCall(Float value) {
        this.costCall = value;
    }

    /**
     * Gets the value of the costKm property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getCostKm() {
        return costKm;
    }

    /**
     * Sets the value of the costKm property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setCostKm(Float value) {
        this.costKm = value;
    }

    /**
     * Gets the value of the costHour property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getCostHour() {
        return costHour;
    }

    /**
     * Sets the value of the costHour property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setCostHour(Float value) {
        this.costHour = value;
    }

    /**
     * Gets the value of the costHourOvertime property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getCostHourOvertime() {
        return costHourOvertime;
    }

    /**
     * Sets the value of the costHourOvertime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setCostHourOvertime(Float value) {
        this.costHourOvertime = value;
    }

}

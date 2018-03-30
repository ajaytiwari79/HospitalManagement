
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
 *         &lt;element name="Mandator" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="StartDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="EndDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="RegionIDs" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AreaOfExpertiseIDs" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TeamIDs" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FieldManagerExtIDs" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UtilisationRatio" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="OpenCallsMode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ComputingTime" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
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
    "mandator",
    "startDate",
    "endDate",
    "regionIDs",
    "areaOfExpertiseIDs",
    "teamIDs",
    "fieldManagerExtIDs",
    "utilisationRatio",
    "openCallsMode",
    "computingTime"
})
@XmlRootElement(name = "Optimize")
public class Optimize {

    @XmlElement(name = "Mandator")
    protected Integer mandator;
    @XmlElement(name = "StartDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startDate;
    @XmlElement(name = "EndDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endDate;
    @XmlElement(name = "RegionIDs")
    protected String regionIDs;
    @XmlElement(name = "AreaOfExpertiseIDs")
    protected String areaOfExpertiseIDs;
    @XmlElement(name = "TeamIDs")
    protected String teamIDs;
    @XmlElement(name = "FieldManagerExtIDs")
    protected String fieldManagerExtIDs;
    @XmlElement(name = "UtilisationRatio")
    protected Integer utilisationRatio;
    @XmlElement(name = "OpenCallsMode")
    protected Integer openCallsMode;
    @XmlElement(name = "ComputingTime")
    protected Integer computingTime;

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
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the regionIDs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegionIDs() {
        return regionIDs;
    }

    /**
     * Sets the value of the regionIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegionIDs(String value) {
        this.regionIDs = value;
    }

    /**
     * Gets the value of the areaOfExpertiseIDs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAreaOfExpertiseIDs() {
        return areaOfExpertiseIDs;
    }

    /**
     * Sets the value of the areaOfExpertiseIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAreaOfExpertiseIDs(String value) {
        this.areaOfExpertiseIDs = value;
    }

    /**
     * Gets the value of the teamIDs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTeamIDs() {
        return teamIDs;
    }

    /**
     * Sets the value of the teamIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTeamIDs(String value) {
        this.teamIDs = value;
    }

    /**
     * Gets the value of the fieldManagerExtIDs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFieldManagerExtIDs() {
        return fieldManagerExtIDs;
    }

    /**
     * Sets the value of the fieldManagerExtIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFieldManagerExtIDs(String value) {
        this.fieldManagerExtIDs = value;
    }

    /**
     * Gets the value of the utilisationRatio property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUtilisationRatio() {
        return utilisationRatio;
    }

    /**
     * Sets the value of the utilisationRatio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUtilisationRatio(Integer value) {
        this.utilisationRatio = value;
    }

    /**
     * Gets the value of the openCallsMode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOpenCallsMode() {
        return openCallsMode;
    }

    /**
     * Sets the value of the openCallsMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOpenCallsMode(Integer value) {
        this.openCallsMode = value;
    }

    /**
     * Gets the value of the computingTime property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getComputingTime() {
        return computingTime;
    }

    /**
     * Sets the value of the computingTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setComputingTime(Integer value) {
        this.computingTime = value;
    }

}


package de.tourenserver;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for FixedCall complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FixedCall">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ExtID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="VTID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Arrival" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="FMExtID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Sequence" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Fixed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="DrivingTime" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Distance" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Info" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FixedCall", propOrder = {
    "extID",
    "vtid",
    "date",
    "arrival",
    "fmExtID",
    "sequence",
    "fixed",
    "state",
    "drivingTime",
    "distance",
    "info"
})
public class FixedCall {

    @XmlElement(name = "ExtID", required = true)
    protected String extID;
    @XmlElement(name = "VTID")
    protected int vtid;
    @XmlElement(name = "Date", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    @XmlElement(name = "Arrival", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar arrival;
    @XmlElement(name = "FMExtID", required = true)
    protected String fmExtID;
    @XmlElement(name = "Sequence")
    protected int sequence;
    @XmlElement(name = "Fixed")
    protected boolean fixed;
    @XmlElement(name = "State")
    protected int state;
    @XmlElement(name = "DrivingTime")
    protected int drivingTime;
    @XmlElement(name = "Distance")
    protected int distance;
    @XmlElement(name = "Info", required = true)
    protected String info;

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
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the arrival property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getArrival() {
        return arrival;
    }

    /**
     * Sets the value of the arrival property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setArrival(XMLGregorianCalendar value) {
        this.arrival = value;
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
     * Gets the value of the sequence property.
     * 
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * Sets the value of the sequence property.
     * 
     */
    public void setSequence(int value) {
        this.sequence = value;
    }

    /**
     * Gets the value of the fixed property.
     * 
     */
    public boolean isFixed() {
        return fixed;
    }

    /**
     * Sets the value of the fixed property.
     * 
     */
    public void setFixed(boolean value) {
        this.fixed = value;
    }

    /**
     * Gets the value of the state property.
     * 
     */
    public int getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     */
    public void setState(int value) {
        this.state = value;
    }

    /**
     * Gets the value of the drivingTime property.
     * 
     */
    public int getDrivingTime() {
        return drivingTime;
    }

    /**
     * Sets the value of the drivingTime property.
     * 
     */
    public void setDrivingTime(int value) {
        this.drivingTime = value;
    }

    /**
     * Gets the value of the distance property.
     * 
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Sets the value of the distance property.
     * 
     */
    public void setDistance(int value) {
        this.distance = value;
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

}

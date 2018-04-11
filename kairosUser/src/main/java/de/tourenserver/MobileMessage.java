
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
 *         &lt;element name="Agent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FMExtID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TimeStamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="MessageType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="VTID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="CallID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Duration" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="NewState" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="substate" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Info" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SearchNextCall" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ScheduleNextCall" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ScheduleCalls" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="SentCallExtIDs" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SentCallVTIDs" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Attachments" type="{http://www.tourenserver.de/}ArrayOfAttachment" minOccurs="0"/>
 *         &lt;element name="TrackPoints" type="{http://www.tourenserver.de/}ArrayOfTrackPoint" minOccurs="0"/>
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
    "agent",
    "fmExtID",
    "timeStamp",
    "messageType",
    "vtid",
    "callID",
    "duration",
    "newState",
    "substate",
    "info",
    "searchNextCall",
    "scheduleNextCall",
    "scheduleCalls",
    "sentCallExtIDs",
    "sentCallVTIDs",
    "attachments",
    "trackPoints"
})
@XmlRootElement(name = "MobileMessage")
public class MobileMessage {

    @XmlElement(name = "Mandator")
    protected Integer mandator;
    @XmlElement(name = "Agent")
    protected String agent;
    @XmlElement(name = "FMExtID", required = true)
    protected String fmExtID;
    @XmlElement(name = "TimeStamp", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timeStamp;
    @XmlElement(name = "MessageType")
    protected int messageType;
    @XmlElement(name = "VTID")
    protected Integer vtid;
    @XmlElement(name = "CallID")
    protected String callID;
    @XmlElement(name = "Duration")
    protected Integer duration;
    @XmlElement(name = "NewState")
    protected Integer newState;
    protected Integer substate;
    @XmlElement(name = "Info")
    protected String info;
    @XmlElement(name = "SearchNextCall")
    protected Boolean searchNextCall;
    @XmlElement(name = "ScheduleNextCall")
    protected Boolean scheduleNextCall;
    @XmlElement(name = "ScheduleCalls")
    protected Integer scheduleCalls;
    @XmlElement(name = "SentCallExtIDs")
    protected String sentCallExtIDs;
    @XmlElement(name = "SentCallVTIDs")
    protected String sentCallVTIDs;
    @XmlElement(name = "Attachments")
    protected ArrayOfAttachment attachments;
    @XmlElement(name = "TrackPoints")
    protected ArrayOfTrackPoint trackPoints;

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
     * Gets the value of the timeStamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimeStamp() {
        return timeStamp;
    }

    /**
     * Sets the value of the timeStamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimeStamp(XMLGregorianCalendar value) {
        this.timeStamp = value;
    }

    /**
     * Gets the value of the messageType property.
     * 
     */
    public int getMessageType() {
        return messageType;
    }

    /**
     * Sets the value of the messageType property.
     * 
     */
    public void setMessageType(int value) {
        this.messageType = value;
    }

    /**
     * Gets the value of the vtid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVTID() {
        return vtid;
    }

    /**
     * Sets the value of the vtid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVTID(Integer value) {
        this.vtid = value;
    }

    /**
     * Gets the value of the callID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCallID() {
        return callID;
    }

    /**
     * Sets the value of the callID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCallID(String value) {
        this.callID = value;
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
     * Gets the value of the newState property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNewState() {
        return newState;
    }

    /**
     * Sets the value of the newState property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNewState(Integer value) {
        this.newState = value;
    }

    /**
     * Gets the value of the substate property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSubstate() {
        return substate;
    }

    /**
     * Sets the value of the substate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSubstate(Integer value) {
        this.substate = value;
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
     * Gets the value of the searchNextCall property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSearchNextCall() {
        return searchNextCall;
    }

    /**
     * Sets the value of the searchNextCall property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSearchNextCall(Boolean value) {
        this.searchNextCall = value;
    }

    /**
     * Gets the value of the scheduleNextCall property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isScheduleNextCall() {
        return scheduleNextCall;
    }

    /**
     * Sets the value of the scheduleNextCall property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setScheduleNextCall(Boolean value) {
        this.scheduleNextCall = value;
    }

    /**
     * Gets the value of the scheduleCalls property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getScheduleCalls() {
        return scheduleCalls;
    }

    /**
     * Sets the value of the scheduleCalls property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setScheduleCalls(Integer value) {
        this.scheduleCalls = value;
    }

    /**
     * Gets the value of the sentCallExtIDs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSentCallExtIDs() {
        return sentCallExtIDs;
    }

    /**
     * Sets the value of the sentCallExtIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSentCallExtIDs(String value) {
        this.sentCallExtIDs = value;
    }

    /**
     * Gets the value of the sentCallVTIDs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSentCallVTIDs() {
        return sentCallVTIDs;
    }

    /**
     * Sets the value of the sentCallVTIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSentCallVTIDs(String value) {
        this.sentCallVTIDs = value;
    }

    /**
     * Gets the value of the attachments property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAttachment }
     *     
     */
    public ArrayOfAttachment getAttachments() {
        return attachments;
    }

    /**
     * Sets the value of the attachments property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAttachment }
     *     
     */
    public void setAttachments(ArrayOfAttachment value) {
        this.attachments = value;
    }

    /**
     * Gets the value of the trackPoints property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTrackPoint }
     *     
     */
    public ArrayOfTrackPoint getTrackPoints() {
        return trackPoints;
    }

    /**
     * Sets the value of the trackPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTrackPoint }
     *     
     */
    public void setTrackPoints(ArrayOfTrackPoint value) {
        this.trackPoints = value;
    }

}

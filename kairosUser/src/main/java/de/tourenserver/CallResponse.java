
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
 *         &lt;element name="CallResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="VTID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="InfoText" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Appointments" type="{http://www.tourenserver.de/}ArrayOfAppointment"/>
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
    "callResult",
    "vtid",
    "infoText",
    "appointments"
})
@XmlRootElement(name = "CallResponse")
public class CallResponse {

    @XmlElement(name = "CallResult")
    protected int callResult;
    @XmlElement(name = "VTID")
    protected int vtid;
    @XmlElement(name = "InfoText", required = true)
    protected String infoText;
    @XmlElement(name = "Appointments", required = true)
    protected ArrayOfAppointment appointments;

    /**
     * Gets the value of the callResult property.
     * 
     */
    public int getCallResult() {
        return callResult;
    }

    /**
     * Sets the value of the callResult property.
     * 
     */
    public void setCallResult(int value) {
        this.callResult = value;
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
     * Gets the value of the infoText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInfoText() {
        return infoText;
    }

    /**
     * Sets the value of the infoText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInfoText(String value) {
        this.infoText = value;
    }

    /**
     * Gets the value of the appointments property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAppointment }
     *     
     */
    public ArrayOfAppointment getAppointments() {
        return appointments;
    }

    /**
     * Sets the value of the appointments property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAppointment }
     *     
     */
    public void setAppointments(ArrayOfAppointment value) {
        this.appointments = value;
    }

}

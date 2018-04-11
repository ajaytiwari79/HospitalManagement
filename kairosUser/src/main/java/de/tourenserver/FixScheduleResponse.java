
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
 *         &lt;element name="FixScheduleResult" type="{http://www.tourenserver.de/}ArrayOfFixedCall"/>
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
    "fixScheduleResult"
})
@XmlRootElement(name = "FixScheduleResponse")
public class FixScheduleResponse {

    @XmlElement(name = "FixScheduleResult", required = true)
    protected ArrayOfFixedCall fixScheduleResult;

    /**
     * Gets the value of the fixScheduleResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfFixedCall }
     *     
     */
    public ArrayOfFixedCall getFixScheduleResult() {
        return fixScheduleResult;
    }

    /**
     * Sets the value of the fixScheduleResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfFixedCall }
     *     
     */
    public void setFixScheduleResult(ArrayOfFixedCall value) {
        this.fixScheduleResult = value;
    }

}

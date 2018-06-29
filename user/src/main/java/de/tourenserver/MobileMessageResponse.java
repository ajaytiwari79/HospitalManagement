
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
 *         &lt;element name="MobileMessageResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NextCalls" type="{http://www.tourenserver.de/}ArrayOfNextCall"/>
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
    "mobileMessageResult",
    "nextCalls"
})
@XmlRootElement(name = "MobileMessageResponse")
public class MobileMessageResponse {

    @XmlElement(name = "MobileMessageResult")
    protected int mobileMessageResult;
    @XmlElement(name = "NextCalls", required = true)
    protected ArrayOfNextCall nextCalls;

    /**
     * Gets the value of the mobileMessageResult property.
     * 
     */
    public int getMobileMessageResult() {
        return mobileMessageResult;
    }

    /**
     * Sets the value of the mobileMessageResult property.
     * 
     */
    public void setMobileMessageResult(int value) {
        this.mobileMessageResult = value;
    }

    /**
     * Gets the value of the nextCalls property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfNextCall }
     *     
     */
    public ArrayOfNextCall getNextCalls() {
        return nextCalls;
    }

    /**
     * Sets the value of the nextCalls property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfNextCall }
     *     
     */
    public void setNextCalls(ArrayOfNextCall value) {
        this.nextCalls = value;
    }

}

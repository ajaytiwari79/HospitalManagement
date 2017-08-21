
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
 *         &lt;element name="ShowCallInfoResult" type="{http://www.tourenserver.de/}CallInfoRec"/>
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
    "showCallInfoResult"
})
@XmlRootElement(name = "ShowCallInfoResponse")
public class ShowCallInfoResponse {

    @XmlElement(name = "ShowCallInfoResult", required = true)
    protected CallInfoRec showCallInfoResult;

    /**
     * Gets the value of the showCallInfoResult property.
     * 
     * @return
     *     possible object is
     *     {@link CallInfoRec }
     *     
     */
    public CallInfoRec getShowCallInfoResult() {
        return showCallInfoResult;
    }

    /**
     * Sets the value of the showCallInfoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link CallInfoRec }
     *     
     */
    public void setShowCallInfoResult(CallInfoRec value) {
        this.showCallInfoResult = value;
    }

}

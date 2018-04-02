
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
 *         &lt;element name="VTID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ExtID" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "vtid",
    "extID"
})
@XmlRootElement(name = "ShowCallInfo")
public class ShowCallInfo {

    @XmlElement(name = "VTID")
    protected Integer vtid;
    @XmlElement(name = "ExtID", required = true)
    protected String extID;

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

}

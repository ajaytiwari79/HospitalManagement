
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
 *         &lt;element name="GeocodeResult" type="{http://www.tourenserver.de/}ArrayOfGeoCodeRec"/>
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
    "geocodeResult"
})
@XmlRootElement(name = "GeocodeResponse")
public class GeocodeResponse {

    @XmlElement(name = "GeocodeResult", required = true)
    protected ArrayOfGeoCodeRec geocodeResult;

    /**
     * Gets the value of the geocodeResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfGeoCodeRec }
     *     
     */
    public ArrayOfGeoCodeRec getGeocodeResult() {
        return geocodeResult;
    }

    /**
     * Sets the value of the geocodeResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfGeoCodeRec }
     *     
     */
    public void setGeocodeResult(ArrayOfGeoCodeRec value) {
        this.geocodeResult = value;
    }

}

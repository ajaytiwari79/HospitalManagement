
package de.tourenserver;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfGeoCodeRec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfGeoCodeRec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GeoCodeRec" type="{http://www.tourenserver.de/}GeoCodeRec" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfGeoCodeRec", propOrder = {
    "geoCodeRec"
})
public class ArrayOfGeoCodeRec {

    @XmlElement(name = "GeoCodeRec")
    protected List<GeoCodeRec> geoCodeRec;

    /**
     * Gets the value of the geoCodeRec property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the geoCodeRec property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGeoCodeRec().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GeoCodeRec }
     * 
     * 
     */
    public List<GeoCodeRec> getGeoCodeRec() {
        if (geoCodeRec == null) {
            geoCodeRec = new ArrayList<GeoCodeRec>();
        }
        return this.geoCodeRec;
    }

}

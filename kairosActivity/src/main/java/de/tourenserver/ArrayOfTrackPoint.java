
package de.tourenserver;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfTrackPoint complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfTrackPoint">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TrackPoint" type="{http://www.tourenserver.de/}TrackPoint" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfTrackPoint", propOrder = {
    "trackPoint"
})
public class ArrayOfTrackPoint {

    @XmlElement(name = "TrackPoint")
    protected List<TrackPoint> trackPoint;

    /**
     * Gets the value of the trackPoint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the trackPoint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTrackPoint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TrackPoint }
     * 
     * 
     */
    public List<TrackPoint> getTrackPoint() {
        if (trackPoint == null) {
            trackPoint = new ArrayList<TrackPoint>();
        }
        return this.trackPoint;
    }

}

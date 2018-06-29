
package de.tourenserver;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfCallInfoRec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCallInfoRec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CallInfoRec" type="{http://www.tourenserver.de/}CallInfoRec" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCallInfoRec", propOrder = {
    "callInfoRec"
})
public class ArrayOfCallInfoRec {

    @XmlElement(name = "CallInfoRec")
    protected List<CallInfoRec> callInfoRec;

    /**
     * Gets the value of the callInfoRec property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the callInfoRec property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCallInfoRec().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CallInfoRec }
     * 
     * 
     */
    public List<CallInfoRec> getCallInfoRec() {
        if (callInfoRec == null) {
            callInfoRec = new ArrayList<CallInfoRec>();
        }
        return this.callInfoRec;
    }

}

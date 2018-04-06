
package de.tourenserver;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfNextCall complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfNextCall">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NextCall" type="{http://www.tourenserver.de/}NextCall" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfNextCall", propOrder = {
    "nextCall"
})
public class ArrayOfNextCall {

    @XmlElement(name = "NextCall")
    protected List<NextCall> nextCall;

    /**
     * Gets the value of the nextCall property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nextCall property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNextCall().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NextCall }
     * 
     * 
     */
    public List<NextCall> getNextCall() {
        if (nextCall == null) {
            nextCall = new ArrayList<NextCall>();
        }
        return this.nextCall;
    }

}

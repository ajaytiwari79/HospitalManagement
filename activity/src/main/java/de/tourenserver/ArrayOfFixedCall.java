
package de.tourenserver;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfFixedCall complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfFixedCall">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FixedCall" type="{http://www.tourenserver.de/}FixedCall" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfFixedCall", propOrder = {
    "fixedCall"
})
public class ArrayOfFixedCall {

    @XmlElement(name = "FixedCall")
    protected List<FixedCall> fixedCall;

    /**
     * Gets the value of the fixedCall property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fixedCall property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFixedCall().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FixedCall }
     * 
     * 
     */
    public List<FixedCall> getFixedCall() {
        if (fixedCall == null) {
            fixedCall = new ArrayList<FixedCall>();
        }
        return this.fixedCall;
    }

}


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
 *         &lt;element name="FieldManagerResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="FMVTID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SX" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SY" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SNID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="EX" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="EY" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ENID" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "fieldManagerResult",
    "fmvtid",
    "sx",
    "sy",
    "snid",
    "ex",
    "ey",
    "enid"
})
@XmlRootElement(name = "FieldManagerResponse")
public class FieldManagerResponse {

    @XmlElement(name = "FieldManagerResult")
    protected int fieldManagerResult;
    @XmlElement(name = "FMVTID")
    protected int fmvtid;
    @XmlElement(name = "SX")
    protected int sx;
    @XmlElement(name = "SY")
    protected int sy;
    @XmlElement(name = "SNID")
    protected int snid;
    @XmlElement(name = "EX")
    protected int ex;
    @XmlElement(name = "EY")
    protected int ey;
    @XmlElement(name = "ENID")
    protected int enid;

    /**
     * Gets the value of the fieldManagerResult property.
     * 
     */
    public int getFieldManagerResult() {
        return fieldManagerResult;
    }

    /**
     * Sets the value of the fieldManagerResult property.
     * 
     */
    public void setFieldManagerResult(int value) {
        this.fieldManagerResult = value;
    }

    /**
     * Gets the value of the fmvtid property.
     * 
     */
    public int getFMVTID() {
        return fmvtid;
    }

    /**
     * Sets the value of the fmvtid property.
     * 
     */
    public void setFMVTID(int value) {
        this.fmvtid = value;
    }

    /**
     * Gets the value of the sx property.
     * 
     */
    public int getSX() {
        return sx;
    }

    /**
     * Sets the value of the sx property.
     * 
     */
    public void setSX(int value) {
        this.sx = value;
    }

    /**
     * Gets the value of the sy property.
     * 
     */
    public int getSY() {
        return sy;
    }

    /**
     * Sets the value of the sy property.
     * 
     */
    public void setSY(int value) {
        this.sy = value;
    }

    /**
     * Gets the value of the snid property.
     * 
     */
    public int getSNID() {
        return snid;
    }

    /**
     * Sets the value of the snid property.
     * 
     */
    public void setSNID(int value) {
        this.snid = value;
    }

    /**
     * Gets the value of the ex property.
     * 
     */
    public int getEX() {
        return ex;
    }

    /**
     * Sets the value of the ex property.
     * 
     */
    public void setEX(int value) {
        this.ex = value;
    }

    /**
     * Gets the value of the ey property.
     * 
     */
    public int getEY() {
        return ey;
    }

    /**
     * Sets the value of the ey property.
     * 
     */
    public void setEY(int value) {
        this.ey = value;
    }

    /**
     * Gets the value of the enid property.
     * 
     */
    public int getENID() {
        return enid;
    }

    /**
     * Sets the value of the enid property.
     * 
     */
    public void setENID(int value) {
        this.enid = value;
    }

}

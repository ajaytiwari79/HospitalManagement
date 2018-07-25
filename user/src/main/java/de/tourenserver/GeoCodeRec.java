
package de.tourenserver;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GeoCodeRec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GeoCodeRec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Country" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ZIP" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="City" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="District" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Street" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="HNr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="HNr2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Province" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProvShort" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="X" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Y" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="GS" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GeoCodeRec", propOrder = {
    "country",
    "zip",
    "city",
    "district",
    "street",
    "hNr",
    "hNr2",
    "province",
    "provShort",
    "x",
    "y",
    "nid",
    "gs"
})
public class GeoCodeRec {

    @XmlElement(name = "Country", required = true)
    protected String country;
    @XmlElement(name = "ZIP", required = true)
    protected String zip;
    @XmlElement(name = "City", required = true)
    protected String city;
    @XmlElement(name = "District", required = true)
    protected String district;
    @XmlElement(name = "Street", required = true)
    protected String street;
    @XmlElement(name = "HNr", required = true)
    protected String hNr;
    @XmlElement(name = "HNr2", required = true)
    protected String hNr2;
    @XmlElement(name = "Province", required = true)
    protected String province;
    @XmlElement(name = "ProvShort", required = true)
    protected String provShort;
    @XmlElement(name = "X")
    protected int x;
    @XmlElement(name = "Y")
    protected int y;
    @XmlElement(name = "NID")
    protected int nid;
    @XmlElement(name = "GS")
    protected int gs;

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountry(String value) {
        this.country = value;
    }

    /**
     * Gets the value of the zip property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZIP() {
        return zip;
    }

    /**
     * Sets the value of the zip property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZIP(String value) {
        this.zip = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the district property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDistrict() {
        return district;
    }

    /**
     * Sets the value of the district property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDistrict(String value) {
        this.district = value;
    }

    /**
     * Gets the value of the street property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the value of the street property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreet(String value) {
        this.street = value;
    }

    /**
     * Gets the value of the hNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHNr() {
        return hNr;
    }

    /**
     * Sets the value of the hNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHNr(String value) {
        this.hNr = value;
    }

    /**
     * Gets the value of the hNr2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHNr2() {
        return hNr2;
    }

    /**
     * Sets the value of the hNr2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHNr2(String value) {
        this.hNr2 = value;
    }

    /**
     * Gets the value of the province property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProvince() {
        return province;
    }

    /**
     * Sets the value of the province property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProvince(String value) {
        this.province = value;
    }

    /**
     * Gets the value of the provShort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProvShort() {
        return provShort;
    }

    /**
     * Sets the value of the provShort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProvShort(String value) {
        this.provShort = value;
    }

    /**
     * Gets the value of the x property.
     * 
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     * 
     */
    public void setX(int value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     * 
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     * 
     */
    public void setY(int value) {
        this.y = value;
    }

    /**
     * Gets the value of the nid property.
     * 
     */
    public int getNID() {
        return nid;
    }

    /**
     * Sets the value of the nid property.
     * 
     */
    public void setNID(int value) {
        this.nid = value;
    }

    /**
     * Gets the value of the gs property.
     * 
     */
    public int getGS() {
        return gs;
    }

    /**
     * Sets the value of the gs property.
     * 
     */
    public void setGS(int value) {
        this.gs = value;
    }

}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.11.08 at 06:50:13 PM CET 
//


package iso.std.iso._20022.tech.xsd.pain_008_001;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Frequency1Code.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Frequency1Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="YEAR"/>
 *     &lt;enumeration value="MNTH"/>
 *     &lt;enumeration value="QURT"/>
 *     &lt;enumeration value="MIAN"/>
 *     &lt;enumeration value="WEEK"/>
 *     &lt;enumeration value="DAIL"/>
 *     &lt;enumeration value="ADHO"/>
 *     &lt;enumeration value="INDA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Frequency1Code")
@XmlEnum
public enum Frequency1Code {

    YEAR,
    MNTH,
    QURT,
    MIAN,
    WEEK,
    DAIL,
    ADHO,
    INDA;

    public String value() {
        return name();
    }

    public static Frequency1Code fromValue(String v) {
        return valueOf(v);
    }

}

package ru.javaops.masterjava.xml.util;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Resources;

import ru.javaops.masterjava.xml.schema.CityType;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;

public class JaxbParserTest {
    private static final JaxbParser JAXB_PARSER = JaxbParser.getInstance(ObjectFactory.class);

    @BeforeClass
    public static void init() {
        try {
            JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPayload() throws Exception {
//        JaxbParserTest.class.getResourceAsStream("/city.xml")
        Payload payload = JAXB_PARSER.unmarshal(
                Resources.getResource("payload.xml").openStream());
        String strPayload = JAXB_PARSER.marshal(payload);
        JAXB_PARSER.validate(strPayload);
        System.out.println(strPayload);
    }

    @Test
    public void testCity() throws Exception {
        JAXBElement<CityType> cityElement = JAXB_PARSER.unmarshal(
                Resources.getResource("city.xml").openStream());
        CityType city = cityElement.getValue();
        JAXBElement<CityType> cityElement2 =
                new JAXBElement<>(new QName("http://javaops.ru", "City"), CityType.class, city);
        String strCity = JAXB_PARSER.marshal(cityElement2);
        JAXB_PARSER.validate(strCity);
        System.out.println(strCity);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        JAXB_PARSER.close();
    }
}
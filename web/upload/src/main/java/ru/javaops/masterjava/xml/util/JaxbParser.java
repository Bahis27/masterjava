package ru.javaops.masterjava.xml.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.xml.sax.SAXException;


/**
 * Marshalling/Unmarshalling JAXB helper
 * XML Facade
 */
public class JaxbParser {

    private static Map<Class, JaxbParser> singletonMap = new ConcurrentHashMap<>();
    private Class clazz;
    private ThreadLocal<JaxbMarshaller> jaxbMarshallerThreadLocal = new ThreadLocal<>();
    private ThreadLocal<JaxbUnmarshaller> jaxbUnmarshallerThreadLocal = new ThreadLocal<>();
    private Schema schema;

    private JaxbParser(Class clazz) {
        this.clazz = clazz;
    }

    public static JaxbParser getInstance(Class clazz) {
        JaxbParser jaxbParser = singletonMap.get(clazz);
        if (jaxbParser == null) {
            jaxbParser = new JaxbParser(clazz);
            singletonMap.put(clazz, jaxbParser);
        }
        return jaxbParser;
    }

    private JaxbUnmarshaller getUnmarshaller() throws JAXBException {
        JaxbUnmarshaller jaxbUnmarshaller = jaxbUnmarshallerThreadLocal.get();
        if (jaxbUnmarshaller == null) {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            jaxbUnmarshaller = new JaxbUnmarshaller(jaxbContext);
            jaxbUnmarshallerThreadLocal.set(jaxbUnmarshaller);
        }
        return jaxbUnmarshaller;
    }

    private JaxbMarshaller getMarshaller() throws JAXBException {
        JaxbMarshaller jaxbMarshaller = jaxbMarshallerThreadLocal.get();
        if (jaxbMarshaller == null) {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            jaxbMarshaller = new JaxbMarshaller(jaxbContext);
            jaxbMarshallerThreadLocal.set(jaxbMarshaller);
        }
        return jaxbMarshaller;
    }

    // Unmarshaller
    public <T> T unmarshal(InputStream is) throws JAXBException {
        return (T) getUnmarshaller().unmarshal(is);
    }

    public <T> T unmarshal(Reader reader) throws JAXBException {
        return (T) getUnmarshaller().unmarshal(reader);
    }

    public <T> T unmarshal(String str) throws JAXBException {
        return (T) getUnmarshaller().unmarshal(str);
    }

    public <T> T unmarshal(XMLStreamReader reader, Class<T> elementClass) throws JAXBException {
        return getUnmarshaller().unmarshal(reader, elementClass);
    }

    // Marshaller
    public void setMarshallerProperty(String prop, Object value) throws JAXBException {
        getMarshaller().setProperty(prop, value);
    }

    public String marshal(Object instance) throws JAXBException {
        return getMarshaller().marshal(instance);
    }

    public void marshal(Object instance, Writer writer) throws JAXBException {
        getMarshaller().marshal(instance, writer);
    }

    public void setSchema(Schema schema) throws JAXBException {
        this.schema = schema;
        getUnmarshaller().setSchema(schema);
        getMarshaller().setSchema(schema);
    }

    public void validate(String str) throws IOException, SAXException {
        validate(new StringReader(str));
    }

    public void validate(Reader reader) throws IOException, SAXException {
        schema.newValidator().validate(new StreamSource(reader));
    }
}

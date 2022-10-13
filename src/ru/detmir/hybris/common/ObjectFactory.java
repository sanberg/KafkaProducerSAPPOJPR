
package ru.detmir.hybris.common;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.detmir.hybris.common package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CommonKafkaMessageRequest_QNAME = new QName("urn:DetMir.ru:Hybris:Common", "CommonKafkaMessageRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.detmir.hybris.common
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CommonKafkaMessageRequest }
     * 
     */
    public CommonKafkaMessageRequest createCommonKafkaMessageRequest() {
        return new CommonKafkaMessageRequest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CommonKafkaMessageRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:DetMir.ru:Hybris:Common", name = "CommonKafkaMessageRequest")
    public JAXBElement<CommonKafkaMessageRequest> createCommonKafkaMessageRequest(CommonKafkaMessageRequest value) {
        return new JAXBElement<CommonKafkaMessageRequest>(_CommonKafkaMessageRequest_QNAME, CommonKafkaMessageRequest.class, null, value);
    }

}

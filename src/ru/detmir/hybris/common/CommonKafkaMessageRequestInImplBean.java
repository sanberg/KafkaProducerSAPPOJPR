package ru.detmir.hybris.common;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.naming.NamingException;

import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.util.Properties;

import com.sap.engine.services.webservices.espbase.configuration.ann.dt.AuthenticationDT;
import com.sap.engine.services.webservices.espbase.configuration.ann.dt.AuthenticationEnumsAuthenticationLevel;
import com.sap.engine.services.webservices.espbase.configuration.ann.dt.TransportGuaranteeDT;
import com.sap.engine.services.webservices.espbase.configuration.ann.dt.TransportGuaranteeEnumsLevel;
import com.sap.engine.services.webservices.espbase.configuration.ann.dt.XIEnabled;
import com.sap.engine.services.webservices.espbase.configuration.ann.rt.TransportBindingRT;
import com.sap.engine.services.webservices.espbase.server.additions.xi.ProviderXIMessageContext;
import com.sap.tc.logging.Location;


import com.sap.engine.interfaces.messaging.api.MessageDirection;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;
import com.sap.engine.services.webservices.espbase.configuration.ann.dt.Addressing;
import com.sap.engine.services.webservices.espbase.configuration.ann.dt.SessionHandlingDT;
import com.sap.engine.services.webservices.espbase.configuration.ann.dt.RelMessagingNW05DTOperation;

import javax.ejb.TransactionAttribute;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

@SessionHandlingDT(enableSession = false)
@Addressing(enabled = true)
@TransportGuaranteeDT(level = TransportGuaranteeEnumsLevel.NONE)
@AuthenticationDT(authenticationLevel = AuthenticationEnumsAuthenticationLevel.BASIC)
@WebService(portName = "CommonKafkaMessageRequest_In_Port", serviceName = "CommonKafkaMessageRequest_In_Service", endpointInterface = "ru.detmir.hybris.common.CommonKafkaMessageRequestIn", targetNamespace = "urn:DetMir.ru:Hybris:Common", wsdlLocation = "META-INF/wsdl/ru/detmir/hybris/common/CommonKafkaMessageRequest_In/CommonKafkaMessageRequest_In.wsdl")
@Stateless
@XIEnabled
@TransportBindingRT(AltPath = "{urn:DetMir.ru:Hybris:Common}CommonKafkaMessageRequest_In")
public class CommonKafkaMessageRequestInImplBean {

    private static void addAuditLogEntry(AuditLogStatus status, String s, MessageMonitor monitor) throws RuntimeException {
        try {
            monitor.addLogEntry(status, s);
        } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @TransactionAttribute()
    @RelMessagingNW05DTOperation(enableWSRM = true)
    public void commonKafkaMessageRequestIn(CommonKafkaMessageRequest commonKafkaMessageRequest) {
        final Location LOG = Location.getLocation(CommonKafkaMessageRequestInImplBean.class);

        //initializing auditlog entries class
        ProviderXIMessageContext msgctx = ProviderXIMessageContext.getInstance();
        String msgId = msgctx.getRequestMessageID().toString();
        MessageMonitor monitor = null;
        try {
            monitor = new MessageMonitor(msgId, MessageDirection.INBOUND);
        } catch (NamingException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException | NoSuchFieldException e) {
            LOG.debugT("CommonKafkaMessageRequest_In_Proxy: AuditLog object isn't assigned. Exception occurred: " + e.getMessage());
            throw new RuntimeException("CommonKafkaMessageRequest_In_Proxy: AuditLog object isn't assigned. Exception occurred: " + e.getMessage());
        }
        addAuditLogEntry(AuditLogStatus.SUCCESS, "CommonKafkaMessageRequest_In_Proxy: Processing started", monitor);

        String server = commonKafkaMessageRequest.getServer();
        Properties properties = new java.util.Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        addAuditLogEntry(AuditLogStatus.SUCCESS, "CommonKafkaMessageRequest_In_Proxy: server is: " + server, monitor);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        try {
            String decodedData = new String(Base64.getDecoder().decode(commonKafkaMessageRequest.getData()));
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(commonKafkaMessageRequest.getTopicName(), decodedData);
            RecordMetadata metadata = producer.send(producerRecord).get();
            addAuditLogEntry(AuditLogStatus.SUCCESS, "CommonKafkaMessageRequest_In_Proxy: message was sent to topic "
                    + commonKafkaMessageRequest.getTopicName() + " with " +
                    "offset: " + metadata.offset() +
                    " timestamp: " + metadata.timestamp(), monitor);
        } catch (Exception e) {
            Throwable t = e;
            while (t.getCause() != null) {
                addAuditLogEntry(AuditLogStatus.ERROR, "CommonKafkaMessageRequest_In_Proxy: " + t.getCause(), monitor);
                t = t.getCause();
            }
            throw new RuntimeException(e.getMessage());
        } finally {
            producer.close();
            addAuditLogEntry(AuditLogStatus.SUCCESS, "CommonKafkaMessageRequest_In_Proxy: producer closed", monitor);
        }
    }
}
package com.iexceed.appzillon.jsonutils;

import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class JsonToXMLConverter {

    //Added by Sourav
    private static Logger log = LoggerFactory.getLoggerFactory().getRestServicesLogger(
    		ServerConstants.LOGGER_RESTFULL_SERVICES, JsonToXMLConverter.class.toString());
    /**
     * Changes made by Sourav on 03-03-2014
     * This method will reorder the xml based on the input xslt using transformer
     *(The xslt must contain the ordering of nodes based on the xml schema) 
     * pXmlInput is the unordered xml
     * @param pXmlInput
     * @param pXslFilePath
     * @return String
     */
        
    public String generateFinalXML(String pXmlInput, String pXslFilePath) {
        log.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Start generateFinalXML");
        String respXML = "";
        try {
            InputStream in = new ByteArrayInputStream(pXmlInput.getBytes());
            Source xmlSource = new StreamSource(in);
            Source xslSource = new StreamSource(new File(pXslFilePath));
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(xslSource);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            transformer.transform(xmlSource, new StreamResult(bout));
            bout.close();
            respXML = bout.toString();
            log.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Response xml    " + respXML);
        } catch (TransformerException ex) {
            log.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "TransformerException  ",  ex);
        } catch (IOException ex) {
            log.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "IOException   ", ex);
        }
        return respXML;
    }

    public static String jsonToXml(String str) {
    	log.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Start JsonToXMLConverter");
        InputStream input = null;
        XMLEventReader reader = null;
        XMLEventWriter writer = null;

        try {
            input = new ByteArrayInputStream(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            log.error("[REST] =>UnsupportedEncodingException:", e1);
        }

        StringWriter output = new StringWriter();

        JsonXMLConfig config = new JsonXMLConfigBuilder().multiplePI(false)
                .build();
        try {
            reader = new JsonXMLInputFactory(config)
                    .createXMLEventReader(input);
            writer = XMLOutputFactory.newInstance()
                    .createXMLEventWriter(output);
            writer.add(reader);

        } catch (XMLStreamException e) {
            log.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "XMLStreamException:", e);

        } finally {
            try {
                output.close();

                if (input != null) {
                    input.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                log.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "IOException:", e);

            } catch (XMLStreamException e) {
                log.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "XMLStreamException:", e);
            }
        }
        return output.toString();
    }
}

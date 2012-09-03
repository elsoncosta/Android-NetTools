package com.pc.nettools.http;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Pietro Caselani
 */
public class SAXResponseHandler extends HttpResponseHandler {

    public void onSuccess(XMLReader xmlReader, InputSource inputSource, AsyncHttpRequest request) {}

    @Override
    public void sendSuccessMessage(ByteArrayOutputStream outputStream, AsyncHttpRequest request) {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();

            ByteArrayInputStream bais = new ByteArrayInputStream(outputStream.toByteArray());

            onSuccess(xmlReader, new InputSource(bais), request);
        } catch (ParserConfigurationException e) {
            onFailure(e, request);
        } catch (SAXException e) {
            onFailure(e, request);
        }
    }
}
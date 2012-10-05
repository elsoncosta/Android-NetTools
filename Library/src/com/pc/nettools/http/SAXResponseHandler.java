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
import java.util.ArrayList;

/**
 * Created by Pietro Caselani
 */
public class SAXResponseHandler extends HttpResponseHandler {
    private XMLReader mReader;
    private InputSource mInputSource;

    public void onSuccess(XMLReader xmlReader, InputSource inputSource, AsyncHttpRequest request, int statusCode) {}

    @Override
    public void onFinish() {
        if (mReader != null && mInputSource != null)
            onSuccess(mReader, mInputSource, mRequest, mStatusCode);
        else
            onFailure(mException, mRequest, mStatusCode);
    }

    @Override
    public Object getResponseObject() {
        ArrayList<Object> objects = new ArrayList<Object>(2);
        objects.add(mReader);
        objects.add(mInputSource);
        return objects;
    }

    @Override
    public void sendSuccessMessage(ByteArrayOutputStream outputStream) {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            mReader = saxParser.getXMLReader();

            ByteArrayInputStream bais = new ByteArrayInputStream(outputStream.toByteArray());

            mInputSource = new InputSource(bais);

        } catch (ParserConfigurationException e) {
            mException = e;
        } catch (SAXException e) {
            mException = e;
        }
    }
}
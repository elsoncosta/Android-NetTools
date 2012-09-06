package com.pc.nettools.http;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Pietro Caselani
 */
public class DOMResponseHandler extends HttpResponseHandler {
    private Document mDocument;

    public void onSuccess(Document document, AsyncHttpRequest request) {}

    @Override
    public void onFinish() {
        if (mDocument != null)
            onSuccess(mDocument, mRequest);
        else
            onFailure(mException, mRequest);
    }

    @Override
    public Object getResponseObject() {
        return mDocument;
    }

    @Override
    public void sendSuccessMessage(ByteArrayOutputStream outputStream) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            mDocument = documentBuilder.parse(new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (ParserConfigurationException e) {
            mException = e;
        } catch (SAXException e) {
            mException = e;
        } catch (IOException e) {
            mException = e;
        }
    }
}
package com.pc.nettools.http;

import android.os.AsyncTask;
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
    private AsyncHttpRequest mRequest;

    public void onSuccess(Document document, AsyncHttpRequest request) {}

    @Override
    public void sendSuccessMessage(ByteArrayOutputStream outputStream, AsyncHttpRequest request) {
        mRequest = request;

        AsyncTask<ByteArrayInputStream, Void, Document> parserTask = new
                AsyncTask<ByteArrayInputStream, Void, Document>() {
                    private Exception mException = null;

                    @Override
                    protected Document doInBackground(ByteArrayInputStream... byteArrayInputStreams) {
                        try {
                            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                            return documentBuilder.parse(byteArrayInputStreams[0]);
                        } catch (ParserConfigurationException e) {
                            mException = e;
                        } catch (SAXException e) {
                            mException = e;
                        } catch (IOException e) {
                            mException = e;
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Document document) {
                        super.onPostExecute(document);

                        if (mException != null) onFailure(mException, mRequest);
                        else onSuccess(document, mRequest);
                    }
                };

        parserTask.execute(new ByteArrayInputStream(outputStream.toByteArray()));
    }
}
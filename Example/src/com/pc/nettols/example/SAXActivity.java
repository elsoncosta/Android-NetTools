package com.pc.nettols.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.pc.nettools.http.AsyncClient;
import com.pc.nettools.http.AsyncHttpRequest;
import com.pc.nettools.http.SAXResponseHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Pietro Caselani
 */
public class SAXActivity extends Activity implements ShoppingsHandler.ShoppingHandlerListener {
    private AsyncClient mClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_activity);

        mClient = new AsyncClient("");
        mClient.setDefaultAuthentication("", "");

        findViewById(R.id.btn_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getXML();
            }
        });
    }

    private void getXML() {
        mClient.get("/shoppings.xml", new SAXResponseHandler() {
            @Override
            public void onSuccess(XMLReader xmlReader, InputSource inputSource, AsyncHttpRequest request) {
                try {
                    parse(xmlReader, inputSource);
                } catch (IOException e) {
                    onFailure(e, request);
                } catch (SAXException e) {
                    onFailure(e, request);
                }
            }

            @Override
            public void onFailure(Exception exception, AsyncHttpRequest request) {
                Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProgress(int progress, AsyncHttpRequest request) {
                ((ProgressBar) findViewById(R.id.progressBar)).setProgress(progress);
            }
        });
    }

    private void parse(XMLReader xmlReader, InputSource inputSource) throws IOException, SAXException {
        ShoppingsHandler shoppingsHandler = new ShoppingsHandler(this);

        xmlReader.setContentHandler(shoppingsHandler);
        xmlReader.parse(inputSource);
    }

    @Override
    public void onParseShoppings(ArrayList<Shopping> shoopings) {
        StringBuilder stringBuilder = new StringBuilder("Shoppings:\n\n");

        for (Shopping shopping : shoopings) {
            String id = String.valueOf(shopping.getId());
            String text = "ID: " + id + "\nNome: " + shopping.getName() + "\n\n";
            stringBuilder.append(text);
        }

        ((TextView) findViewById(R.id.textView_content)).setText(stringBuilder.toString());
    }
}
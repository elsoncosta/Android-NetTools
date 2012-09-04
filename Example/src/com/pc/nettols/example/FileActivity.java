package com.pc.nettols.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.pc.nettools.http.AsyncClient;
import com.pc.nettools.http.AsyncHttpRequest;
import com.pc.nettools.http.DOMResponseHandler;
import com.pc.nettools.http.FileResponseHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Pietro Caselani
 */
public class FileActivity extends Activity {

    private AsyncClient mClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_activity);

        mClient = new AsyncClient(Utils.SHOPPING_LINK);
        mClient.setDefaultAuthentication(Utils.SHOPPING_USER, Utils.SHOPPING_PASSWORD);

        findViewById(R.id.btn_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFile();
            }
        });
    }

    private void getFile() {
        String path = Utils.SHOPPING_PATH_XML;

        String response = getCacheDir().getAbsolutePath().concat(path);

        mClient.get(path, new FileResponseHandler(response) {
            @Override
            public void onSuccess(File file, AsyncHttpRequest request) {
                parseXML(file, request);
            }

            @Override
            public void onFailure(Exception exception, AsyncHttpRequest request) {
                Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void parseXML(File file, AsyncHttpRequest request) {
        DOMResponseHandler domResponseHandler = new DOMResponseHandler() {
            @Override
            public void onSuccess(Document document, AsyncHttpRequest request) {
                Element shoppingsElement = document.getDocumentElement();
                NodeList nodeShoppings = shoppingsElement.getElementsByTagName("shopping");

                ArrayList<Shopping> shoppings = new ArrayList<Shopping>(nodeShoppings.getLength());

                for (int i = 0; i < nodeShoppings.getLength(); i++) {
                    Element shoppingElement = (Element) nodeShoppings.item(i);
                    Element idElement = (Element) shoppingElement.getElementsByTagName("id").item(0);
                    Element nameElement = (Element) shoppingElement.getElementsByTagName("name").item(0);
                    int id = Integer.parseInt(idElement.getChildNodes().item(0).getNodeValue());
                    String name = nameElement.getChildNodes().item(0).getNodeValue();

                    Shopping shopping = new Shopping();
                    shopping.setId(id);
                    shopping.setName(name);

                    shoppings.add(shopping);
                }
            }

            @Override
            public void onFailure(Exception exception, AsyncHttpRequest request) {
                Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show();
            }
        };
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte data[] = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = fileInputStream.read(data)) > 0)
                baos.write(data, 0, bytesRead);

            domResponseHandler.sendSuccessMessage(baos, null);
        } catch (FileNotFoundException e) {
            domResponseHandler.sendFailureMessage(e, null);
        } catch (IOException e) {
            domResponseHandler.sendFailureMessage(e, null);
        }
    }
}
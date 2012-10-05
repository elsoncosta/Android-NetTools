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

        mClient = new AsyncClient(Utils.BASIC_LINK);
        mClient.setDefaultAuthentication(Utils.BASIC_USER, Utils.BASIC_PASS);

        findViewById(R.id.btn_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFile();
            }
        });
    }

    private void getFile() {
        String path = Utils.BASIC_PATH_XML;

        String response = getCacheDir().getAbsolutePath().concat(path);

        mClient.get(path, new FileResponseHandler(response) {
            @Override
            public void onSuccess(File file, AsyncHttpRequest request, int statusCode) {
                Toast.makeText(getApplicationContext(), file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception exception, AsyncHttpRequest request, int statusCode) {
                Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
package com.pc.nettols.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.pc.nettools.http.AsyncClient;
import com.pc.nettools.http.AsyncHttpRequest;
import com.pc.nettools.http.JSONResponseHandler;

import java.util.ArrayList;

/**
 * Created by Pietro Caselani
 */
public class BasicAuthActivity extends Activity {
    private static final String LINK = "";
    private static final String USER = "";
    private static final String PASSWORD = "";

    private AsyncClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_activity);

        client = new AsyncClient(LINK);
        client.setDefaultAuthentication(USER, PASSWORD);

        findViewById(R.id.btn_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVideos();
            }
        });
    }

    private void getVideos() {
        String path = "/videos.json";

        client.get(path, new JSONResponseHandler() {
            @Override
            public void onSuccess(ArrayList json, AsyncHttpRequest request) {
                ((TextView) findViewById(R.id.textView_content)).setText(json.toString());
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
}
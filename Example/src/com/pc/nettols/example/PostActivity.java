package com.pc.nettols.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.pc.nettools.http.AsyncClient;
import com.pc.nettools.http.AsyncHttpRequest;
import com.pc.nettools.http.HttpResponseHandler;
import com.pc.nettools.http.StringResponseHandler;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Created by Pietro Caselani
 */
public class PostActivity extends Activity {
    private AsyncClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_activity);

        client = new AsyncClient(Utils.FLIP_LINK);

        findViewById(R.id.btn_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRequest();
            }
        });
    }

    private void startRequest() {
        String path = Utils.FLIP_PATH_POST;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("a", "6.4");
        params.put("b", "2.2");

        client.post(path, params, new StringResponseHandler() {
            @Override
            public void onSuccess(String string, AsyncHttpRequest request) {
                ((TextView) findViewById(R.id.textView_content)).setText(string);
            }

            @Override
            public void onFailure(Exception exception, AsyncHttpRequest request) {
                Toast.makeText(getApplication(), exception.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProgress(int progress, AsyncHttpRequest request) {
                ((ProgressBar) findViewById(R.id.progressBar)).setProgress(progress);
            }
        });
    }
}
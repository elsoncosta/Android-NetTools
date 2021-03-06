package com.pc.nettools.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.pc.nettools.http.AsyncHttpRequest;
import com.pc.nettools.http.JSONResponseHandler;

import java.util.ArrayList;

/**
 * Created by Pietro Caselani
 */
public class TasksActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_activity);

        findViewById(R.id.task_json).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AsyncHttpRequest.request(Utils.JSON_TASK, new JSONResponseHandler() {
                    @Override
                    public void onSuccess(Object json, AsyncHttpRequest request, int statusCode) {
                    }

                    @Override
                    public void onFailure(Exception exception, AsyncHttpRequest request, int statusCode) {
                    }

                    @Override
                    public void onProgress(int progress, AsyncHttpRequest request) {
                    }
                });
            }
        });
    }
}
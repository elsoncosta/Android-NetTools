package com.pc.nettols.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.pc.nettools.AsyncRequest;
import com.pc.nettools.http.JSONResponseHandler;
import com.pc.nettools.tasks.HttpTask;

/**
 * Created by Pietro Caselani
 */
public class TasksActivity extends Activity implements AsyncRequest.AsyncRequestListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_activity);

        findViewById(R.id.task_json).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpTask jsonTask = new HttpTask(JSONResponseHandler.class);
                jsonTask.setRequestListener(TasksActivity.this);
                jsonTask.start(Utils.JSON_TASK);
            }
        });
    }

    @Override
    public void onSuccess(Object o, AsyncRequest request) {
        ((TextView) findViewById(R.id.textView_task)).setText(o.toString());
    }

    @Override
    public void onFailure(Exception exception, AsyncRequest request) {
        ((TextView) findViewById(R.id.textView_task)).setText(exception.toString());
    }

    @Override
    public void onProgressChanged(int progress, AsyncRequest request) {
        Log.i("PROGRESS-TASK", String.valueOf(progress) + "%");
    }

    @Override
    public void onCancelled(AsyncRequest request) {

    }
}
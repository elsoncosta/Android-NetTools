package com.pc.nettools.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.pc.nettools.http.AsyncClient;
import com.pc.nettools.http.AsyncHttpRequest;
import com.pc.nettools.http.DOMResponseHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by Pietro Caselani
 */
public class DOMActivity extends Activity {

    private AsyncClient mClient;
    private AsyncHttpRequest mRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_activity);

        mClient = new AsyncClient(Utils.BASIC_LINK);
        mClient.setDefaultAuthentication(Utils.BASIC_USER, Utils.BASIC_PASS);

        findViewById(R.id.btn_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getXML();
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }

    public void cancel() {
        if (mRequest != null)
            mRequest.cancel();
    }

    public void getXML() {
        mRequest = mClient.get(Utils.BASIC_PATH_XML, new DOMResponseHandler() {
            @Override
            public void onSuccess(Document document, AsyncHttpRequest request, int statusCode) {
                parse(document);
            }

            @Override
            public void onFailure(Exception exception, AsyncHttpRequest request, int statusCode) {
                Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProgress(int progress, AsyncHttpRequest request) {
                ((ProgressBar) findViewById(R.id.progressBar)).setProgress(progress);
            }
        });
    }

    private void parse(Document document) {
        Element firstEvent = (Element) document.getElementsByTagName("shopping").item(0);
        int id = Integer.parseInt(firstEvent.getElementsByTagName("id").item(0).getChildNodes().item(0).getNodeValue());
        String title = firstEvent.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();

        String text = String.format("Shopping:\nId = %d\nNome = %s", id, title);

        ((TextView) findViewById(R.id.textView_content)).setText(text);
    }
}
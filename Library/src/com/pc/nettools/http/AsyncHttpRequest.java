package com.pc.nettools.http;

import com.pc.nettools.AsyncRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Pietro Caselani
 */
public class AsyncHttpRequest extends AsyncRequest<HttpURLConnection, Void> {
    private HttpResponseHandler mResponseHandler;
    private InputStream mInputStream;
    private int mStatusCode, mContentLength;
    private HttpURLConnection mConnection;

    public AsyncHttpRequest(HttpResponseHandler responseHandler) {
        mResponseHandler = responseHandler;
        mStatusCode = 0;
        mContentLength = 0;
        mException = null;
    }

    public URL getURL() {
        return mConnection != null ? mConnection.getURL() : null;
    }

    @Override
    public Void executeTask(HttpURLConnection connection) {
        try {
            mConnection = connection;
            mConnection.connect();

            mInputStream = mConnection.getInputStream();
            mContentLength = mConnection.getContentLength();
            mStatusCode = mConnection.getResponseCode();
        } catch (IOException e) {
            mException = e;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (mResponseHandler != null)
            mResponseHandler.sendResponse(mInputStream, mStatusCode, mContentLength, mException, this);

        mConnection.disconnect();
    }
}

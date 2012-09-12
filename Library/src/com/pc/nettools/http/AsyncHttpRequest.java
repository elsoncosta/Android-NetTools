package com.pc.nettools.http;

import com.pc.nettools.AsyncRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

    public static AsyncHttpRequest request(String link, HttpResponseHandler responseHandler) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            AsyncHttpRequest request = new AsyncHttpRequest(responseHandler);
            request.start(connection);
            return request;
        } catch (MalformedURLException e) {
            if (responseHandler != null) responseHandler.onFailure(e, null);
        } catch (IOException e) {
            if (responseHandler != null) responseHandler.onFailure(e, null);
        }
        return null;
    }

    public URL getURL() {
        return mConnection != null ? mConnection.getURL() : null;
    }

    protected void updateProgress(int progress) {
        publishProgress(progress);
    }

    @Override
    public Void executeTask(HttpURLConnection connection) {
        try {
            mConnection = connection;
            connection.connect();

            if (mResponseHandler != null)
                mResponseHandler.sendResponse(connection.getInputStream(), connection.getResponseCode(),
                        connection.getContentLength(), mException, this);
        } catch (IOException e) {
            if (mResponseHandler != null)
                mResponseHandler.sendResponse(null, 0, -1, e, this);
            mException = e;
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return null;
    }

    @Override
    public boolean cancel() {
        if (mResponseHandler != null)
            mResponseHandler.cancel();
        return super.cancel();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (mResponseHandler != null)
            mResponseHandler.onFinish();
    }
}

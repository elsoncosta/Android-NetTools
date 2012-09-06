package com.pc.nettools.tasks;

import com.pc.nettools.AsyncRequest;
import com.pc.nettools.http.HttpResponseHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Pietro Caselani
 */
public class HttpTask extends AsyncRequest<String, Object> {
    private HttpResponseHandler mResponseHandler;

    public HttpTask(Class<? extends HttpResponseHandler> responseHandlerClass) {
        if (responseHandlerClass == null)
            throw new RuntimeException("response handler can't be null");
        try {
            mResponseHandler = responseHandlerClass.newInstance();
        } catch (IllegalAccessException e) {
        } catch (InstantiationException e) {
        }
    }

    @Override
    public Object executeTask(String s) {
        try {
            URL url = new URL(s);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            int contentLength = connection.getContentLength();

            boolean shouldUpdateProgress = contentLength > 0;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte data[] = new byte[1024];
            int bytesRead = 0, offset = 0, progress = 0;

            while ((bytesRead = inputStream.read(data)) > 0 && !isCancelled()) {
                baos.write(data, 0, bytesRead);
                if (shouldUpdateProgress) {
                    offset += bytesRead;
                    progress = (100 * offset) / contentLength;
                    publishProgress(progress);
                }
            }

            baos.flush();
            baos.close();

            mResponseHandler.sendSuccessMessage(baos);
            return mResponseHandler.getResponseObject();
        } catch (MalformedURLException e) {
            mException = e;
        } catch (IOException e) {
            mException = e;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        mResponseHandler.onFinish();
    }
}
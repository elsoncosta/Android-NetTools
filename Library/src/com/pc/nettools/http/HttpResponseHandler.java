package com.pc.nettools.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Pietro Caselani
 */
public class HttpResponseHandler {
    private boolean mIsCancelled;

    public HttpResponseHandler() {
        mIsCancelled = false;
    }

    public void onSuccess(ByteArrayOutputStream outputStream, AsyncHttpRequest request) {}
    public void onFailure(Exception exception, AsyncHttpRequest request) {}
    public void onProgress(int progress, AsyncHttpRequest request) {}
    public void onCanceled() {}

    public void sendSuccessMessage(ByteArrayOutputStream outputStream, AsyncHttpRequest request) {
        onSuccess(outputStream, request);
    }

    public void sendFailureMessage(Exception exception, AsyncHttpRequest request) {
        onFailure(exception, request);
    }

    final void cancel() {
        mIsCancelled = true;
        onCanceled();
    }

    final void sendResponse(InputStream inputStream, int statusCode, int contentLength,
                            Exception exception, AsyncHttpRequest request) {
        if (exception == null) {
            if (inputStream != null) {
                boolean shouldUpdateProgress = contentLength > 0;

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte data[] = new byte[1024];
                int bytesRead = 0, offset = 0, progress = 0;

                try {
                    while ((bytesRead = inputStream.read(data)) > 0 && !mIsCancelled) {
                        baos.write(data, 0, bytesRead);
                        if (shouldUpdateProgress) {
                            offset += bytesRead;
                            progress = (100 * offset) / contentLength;
                            onProgress(progress, request);
                        }
                    }

                    baos.flush();
                    baos.close();

                    sendSuccessMessage(baos, request);
                } catch (IOException e) {
                    sendFailureMessage(e, request);
                }
            } else sendFailureMessage(null, request);
        } else sendFailureMessage(exception, request);
    }
}
package com.pc.nettools.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Pietro Caselani
 */
public abstract class HttpResponseHandler {
    private boolean mIsCancelled;
    protected int mStatusCode;
    protected Exception mException;
    protected AsyncHttpRequest mRequest;

    public HttpResponseHandler() {
        mIsCancelled = false;
    }

    public void onSuccess(ByteArrayOutputStream outputStream, AsyncHttpRequest request, int statusCode) {}
    public void onFailure(Exception exception, AsyncHttpRequest request, int statusCode) {}
    public void onProgress(int progress, AsyncHttpRequest request) {}
    public void onCanceled() {}

    public abstract void onFinish();

    public abstract void sendSuccessMessage(ByteArrayOutputStream outputStream);
    public abstract Object getResponseObject();

    public final void sendFailureMessage(Exception exception) {
        mException = exception;
    }

    final void cancel() {
        mIsCancelled = true;
        onCanceled();
    }

    final void sendResponse(InputStream inputStream, int statusCode, int contentLength,
                            Exception exception, AsyncHttpRequest request) {
        mRequest = request;
        mStatusCode = statusCode;
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
                            request.updateProgress(progress);
                        }
                    }

                    baos.flush();
                    baos.close();

                    sendSuccessMessage(baos);
                } catch (IOException e) {
                    sendFailureMessage(e);
                }
            } else sendFailureMessage(null);
        } else sendFailureMessage(exception);
    }
}
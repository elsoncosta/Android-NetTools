package com.pc.nettools.http;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by Pietro Caselani
 */
public class StringResponseHandler extends HttpResponseHandler {
    private String mString;

    public void onSuccess(String string, AsyncHttpRequest request, int statusCode) {}

    @Override
    public void onFinish() {
        if (mString != null)
            onSuccess(mString, mRequest, mStatusCode);
        else
            onFailure(mException, mRequest, mStatusCode);
    }

    @Override
    public Object getResponseObject() {
        return mString;
    }

    @Override
    public void sendSuccessMessage(ByteArrayOutputStream outputStream) {
        try {
            mString = outputStream.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            mException = e;
        }
    }
}

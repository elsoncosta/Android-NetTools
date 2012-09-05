package com.pc.nettools.http;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by Pietro Caselani
 */
public class StringResponseHandler extends HttpResponseHandler {
    private String mString;

    public void onSuccess(String string, AsyncHttpRequest request) {}

    @Override
    public void onFinish() {
        if (mString != null)
            onSuccess(mString, mRequest);
        else
            onFailure(mException, mRequest);
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

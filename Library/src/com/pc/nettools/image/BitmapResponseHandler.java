package com.pc.nettools.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.pc.nettools.http.AsyncHttpRequest;
import com.pc.nettools.http.HttpResponseHandler;

import java.io.ByteArrayOutputStream;

/**
 * Created by Pietro Caselani
 */
public class BitmapResponseHandler extends HttpResponseHandler {

    public void onSuccess(Bitmap bitmap, AsyncHttpRequest request) {}

    @Override
    public void sendSuccessMessage(ByteArrayOutputStream outputStream, AsyncHttpRequest request) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size());
        if (bitmap != null)
            onSuccess(bitmap, request);
        else
            onFailure(new Exception("can't create the bitmap"), request);
    }
}
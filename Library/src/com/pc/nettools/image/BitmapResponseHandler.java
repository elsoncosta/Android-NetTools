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
    private Bitmap mBitmap;

    public void onSuccess(Bitmap bitmap, AsyncHttpRequest request, int statusCode) {}

    @Override
    public void onFinish() {
        if (mBitmap != null)
            onSuccess(mBitmap, mRequest, mStatusCode);
        else
            onFailure(mException, mRequest, mStatusCode);
    }

    @Override
    public Object getResponseObject() {
        return mBitmap;
    }

    @Override
    public void sendSuccessMessage(ByteArrayOutputStream outputStream) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size());
        if (bitmap != null)
            mBitmap = bitmap;
        else
            mException = new Exception("can't create the bitmap");
    }
}
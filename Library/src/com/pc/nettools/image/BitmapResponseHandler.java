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

    public void onSuccess(Bitmap bitmap, AsyncHttpRequest request) {}

    @Override
    public void onFinish() {
        if (mBitmap != null)
            onSuccess(mBitmap, mRequest);
        else
            onFailure(mException, mRequest);
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
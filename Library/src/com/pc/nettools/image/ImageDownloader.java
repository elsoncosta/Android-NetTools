package com.pc.nettools.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.pc.nettools.http.AsyncHttpRequest;
import java.util.WeakHashMap;

/**
 * Created by Pietro Caselani
 */
public class ImageDownloader {
    private WeakHashMap<ImageView, AsyncHttpRequest> mRequests;
    private ImageCache mImageCache;

    public ImageDownloader(Context context, ImageCache.CacheMode cacheMode) {
        mImageCache = new ImageCache(context, cacheMode);
        mRequests = new WeakHashMap<ImageView, AsyncHttpRequest>();
    }

    public ImageDownloader(Context context) {
        this(context, ImageCache.CacheMode.DEFAULT);
    }

    public void download(String link, ImageView imageView) {
        Bitmap image = mImageCache.get(link);

        if (image == null) forceDownload(link, imageView);
        else {
            cancel(link, imageView);
            imageView.setImageBitmap(image);
        }
    }

    public boolean cancel(ImageView imageView) {
        AsyncHttpRequest request = mRequests.get(imageView);
        return request != null && request.cancel();
    }

    private void forceDownload(String link, final ImageView imageView) {
        if (link == null) {
            imageView.setImageDrawable(null);
            return;
        }

        if (cancel(link, imageView)) {
            imageView.setImageBitmap(null);

            AsyncHttpRequest request = AsyncHttpRequest.request(link, new BitmapResponseHandler() {
                @Override
                public void onSuccess(Bitmap bitmap, AsyncHttpRequest request, int statusCode) {
                    mImageCache.put(request.getURL().toString(), bitmap);

                    AsyncHttpRequest otherRequest = mRequests.get(imageView);

                    if (request == otherRequest) imageView.setImageBitmap(bitmap);
                }
            });

            mRequests.put(imageView, request);
        }
    }

    private boolean cancel(String link, ImageView imageView) {
        AsyncHttpRequest request = mRequests.get(imageView);

        if (request != null) {
            String bitmapLink = request.getURL() != null ? request.getURL().toString() : null;
            if (bitmapLink == null || !bitmapLink.equals(link)) request.cancel();
            else return false;
        }

        return true;
    }
}
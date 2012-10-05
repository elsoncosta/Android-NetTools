package com.pc.nettools.image;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.pc.nettools.http.AsyncClient;
import com.pc.nettools.http.AsyncHttpRequest;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Pietro Caselani
 */
public class ImageDownloader {

    private HashMap<String, Bitmap> mCache;
    private AsyncClient mClient;

    public ImageDownloader() {
        mCache = new HashMap<String, Bitmap>();
        mClient = new AsyncClient();
    }

    public void download(String link, ImageView imageView) {
        Bitmap image = getCachedImage(link);

        if (image == null) forceDownload(link, imageView);
        else {
            cancel(link, imageView);
            imageView.setImageBitmap(image);
        }
    }

    public boolean cancel(ImageView imageView) {
        AsyncHttpRequest request = getBitmapDownloaderTask(imageView);
        return request != null && request.cancel();
    }

    private void forceDownload(String link, final ImageView imageView) {
        if (link == null) {
            imageView.setImageDrawable(null);
            return;
        }

        if (cancel(link, imageView)) {
            AsyncHttpRequest request = mClient.get(link, new BitmapResponseHandler() {
                @Override
                public void onSuccess(Bitmap bitmap, AsyncHttpRequest request, int statusCode) {
                    cacheImage(request.getURL().toString(), bitmap);

                    AsyncHttpRequest otherRequest = getBitmapDownloaderTask(imageView);

                    if (request == otherRequest && imageView != null) imageView.setImageBitmap(bitmap);
                }
            });

            DownloadedDrawable downloadedDrawable = new DownloadedDrawable(request);
            imageView.setImageDrawable(downloadedDrawable);
        }
    }

    private boolean cancel(String link, ImageView imageView) {
        AsyncHttpRequest request = getBitmapDownloaderTask(imageView);

        if (request != null) {
            String bitmapLink = request.getURL() != null ? request.getURL().toString() : null;
            if (bitmapLink == null || !bitmapLink.equals(link)) request.cancel();
            else return false;
        }

        return true;
    }

    private Bitmap getCachedImage(String link) {
        return mCache.get(link);
    }

    private void cacheImage(String url, Bitmap bitmap) {
        if (bitmap != null) mCache.put(url, bitmap);
    }

    private static AsyncHttpRequest getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable != null && drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }

        return null;
    }

    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<AsyncHttpRequest> mRequestReference;

        DownloadedDrawable(AsyncHttpRequest request) {
            super(Color.BLACK);
            mRequestReference = new WeakReference<AsyncHttpRequest>(request);
        }

        public AsyncHttpRequest getBitmapDownloaderTask() {
            return mRequestReference.get();
        }
    }
}
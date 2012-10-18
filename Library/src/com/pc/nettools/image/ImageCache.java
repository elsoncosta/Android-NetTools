package com.pc.nettools.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.*;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Pietro Caselani
 */
public class ImageCache {
    public enum CacheMode {
        DEFAULT,
        MEMORY,
        NONE
    }

    private static final String CACHE_FOLDER = "/com.pc.nettools/";

    private ConcurrentHashMap<String, WeakReference<Bitmap>> mMemoryCache;
    private ExecutorService mExecutorService;
    private CacheMode mCacheMode;
    private String mDiskCachePath;
    private boolean mDiskCacheEnabled;

    public ImageCache(Context context, CacheMode cacheMode) {
        mCacheMode = cacheMode;

        mMemoryCache = new ConcurrentHashMap<String, WeakReference<Bitmap>>();

        mDiskCachePath = context.getCacheDir().getAbsolutePath().concat(CACHE_FOLDER);

        File cacheFolder = new File(mDiskCachePath);

        if (!cacheFolder.exists()) cacheFolder.mkdirs();

        mDiskCacheEnabled = cacheFolder.exists() && (cacheMode == CacheMode.DEFAULT);

        mExecutorService = Executors.newSingleThreadExecutor();
    }

    public void put(String link, Bitmap bitmap) {
        if (mCacheMode != CacheMode.NONE && bitmap != null && link != null) {
            cacheOnMemory(link, bitmap);
            if (mDiskCacheEnabled)
                cacheOnDisk(link, bitmap);
        }
    }

    public Bitmap get(String link) {
        if (mCacheMode != CacheMode.NONE && link != null) {
            Bitmap bitmap = null;
            bitmap = getBitmapFromMemory(link);

            if (bitmap == null && mCacheMode == CacheMode.DEFAULT) {
                bitmap = getBitmapFromDisk(link);
                if (bitmap != null)
                    cacheOnMemory(link, bitmap);
            }

            return bitmap;
        }

        return null;
    }

    public void remove(String link) {
        if (link == null) return;

        mMemoryCache.remove(getCacheKey(link));

        File file = new File(getFilePath(link));
        if (file.exists() && file.isFile())
            file.delete();
    }

    public void clear() {
        mMemoryCache.clear();

        File cacheFolder = new File(mDiskCachePath);
        if (cacheFolder.exists() && cacheFolder.isDirectory()) {
            File[] cachedImages = cacheFolder.listFiles();
            for (File file : cachedImages)
                if (file.exists() && file.isFile())
                    file.delete();
        }
    }

    private void cacheOnMemory(String link, Bitmap bitmap) {
        mMemoryCache.put(getCacheKey(link), new WeakReference<Bitmap>(bitmap));
    }

    private void cacheOnDisk(final String link, final Bitmap bitmap) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                BufferedOutputStream outputStream = null;
                File file = new File(mDiskCachePath, getCacheKey(link));
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    outputStream = new BufferedOutputStream(fileOutputStream, 2048);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private Bitmap getBitmapFromMemory(String link) {
        WeakReference<Bitmap> bitmapRef = mMemoryCache.get(getCacheKey(link));
        return bitmapRef != null ? bitmapRef.get() : null;
    }

    private Bitmap getBitmapFromDisk(String link) {
        String filePath = getFilePath(link);
        File file = new File(filePath);
        return file.exists() ? BitmapFactory.decodeFile(filePath) : null;
    }

    private String getFilePath(String link) {
        return mDiskCachePath.concat(getCacheKey(link));
    }

    private String getCacheKey(String link) {
        if(link == null) throw new RuntimeException("Null url passed in");

        return link.replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
    }
}

package com.pc.nettools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Looper;

/**
 * Created by Pietro Caselani
 */
public abstract class AsyncRequest<Param, Result> extends AsyncTask<Param, Integer, Result> {
    private AsyncRequestListener mRequestListener;

    protected Exception mException;

    public AsyncRequestListener getRequestListener() {
        return mRequestListener;
    }

    public void setRequestListener(AsyncRequestListener requestListener) {
        mRequestListener = requestListener;
    }

    public void start(Param param) {
        execute(param);
    }

    public boolean cancel() {
        return cancel(true);
    }

    public abstract Result executeTask(Param param);

    public static void registerNetworkReceiver(Context context, BroadcastReceiver receiver) {
        if (context != null && receiver != null && !receiver.isOrderedBroadcast()) {
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(receiver, intentFilter);
        }
    }
    public static void unregisterNetworkReceiver(Context context, BroadcastReceiver receiver) {
        if (context != null && receiver != null && receiver.isOrderedBroadcast())
            context.unregisterReceiver(receiver);
    }

    public static NetworkInfo getActiveNetworkInfo(Context context) {
        if (context == null) return null;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isMainThread() {
        return Looper.myLooper() != null && Looper.myLooper() == Looper.getMainLooper();
    }

    @Override
    protected final Result doInBackground(Param... params) {
        return executeTask(params[0]);
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);

        if (mRequestListener != null) {
            if (mException == null && result != null)
                mRequestListener.onSuccess(result, this);
            else if (mException == null)
                mException = new Exception("result is null");
            if (mException != null && result == null)
                mRequestListener.onFailure(mException, this);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        if (mRequestListener != null) mRequestListener.onProgressChanged(values[0], this);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        if (mRequestListener != null) mRequestListener.onCancelled(this);
    }

    public interface AsyncRequestListener<Result> {
        public void onSuccess(Result result, AsyncRequest request);
        public void onFailure(Exception exception, AsyncRequest request);
        public void onProgressChanged(int progress, AsyncRequest request);
        public void onCancelled(AsyncRequest request);
    }
}

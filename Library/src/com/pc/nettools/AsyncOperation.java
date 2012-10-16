package com.pc.nettools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Looper;

import java.util.ArrayList;

/**
 * Created by Pietro Caselani
 */
public abstract class AsyncOperation<Param, Result> extends AsyncTask<Param, Integer, Result> {
    private AsyncOperationListener mOperationListener;
    private ArrayList<AsyncOperationObservable> mObservers;

    protected Exception mException;

    protected AsyncOperation() {
        mObservers = new ArrayList<AsyncOperationObservable>();
    }

    public void addOperationObserver(AsyncOperationObservable observer) {
        mObservers.add(observer);
    }

    public void removeOperationObserver(AsyncOperationObservable observer) {
        mObservers.remove(observer);
    }

    public AsyncOperationListener getOperationListener() {
        return mOperationListener;
    }

    public void setOperationListener(AsyncOperationListener requestListener) {
        mOperationListener = requestListener;
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

        if (mOperationListener != null) {
            if (mException == null && result != null)
                mOperationListener.onSuccess(result, this);
            else if (mException == null)
                mException = new Exception("result is null");
            if (mException != null && result == null)
                mOperationListener.onFailure(mException, this);
        }

        for (AsyncOperationObservable observer : mObservers)
            observer.onOperationFinish(this);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        if (mOperationListener != null) mOperationListener.onProgressChanged(values[0], this);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        if (mOperationListener != null) mOperationListener.onCancelled(this);

        for (AsyncOperationObservable observer : mObservers)
            observer.onOperationFinish(this);
    }

    public interface AsyncOperationListener<Result> {
        public void onSuccess(Result result, AsyncOperation operation);
        public void onFailure(Exception exception, AsyncOperation operation);
        public void onProgressChanged(int progress, AsyncOperation operation);
        public void onCancelled(AsyncOperation operation);
    }

    public interface AsyncOperationObservable {
        public void onOperationFinish(AsyncOperation operation);
    }
}

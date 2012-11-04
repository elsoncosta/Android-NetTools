package com.pc.nettools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Looper;

/**
 * Created by Pietro Caselani
 */
public abstract class AsyncOperation<Param, Result> extends AsyncTask<Param, Integer, Result> {
    public static final String EXTRA_OPERATION_ID = "com.pc.nettools.AsyncOperation.Id";
    public static final String ACTION_OPERATION_START = "com.pc.nettools.AsyncOperation.Start";
    public static final String ACTION_OPERATION_FINISH = "com.pc.nettools.AsyncOperation.Finish";
    public static final String ACTION_OPERATION_CANCEL = "com.pc.nettools.AsyncOperation.Cancel";

    private AsyncOperationListener mOperationListener;
    private Context mContext;
    private int mId;
    protected Exception mException;

    public AsyncOperationListener getOperationListener() {
        return mOperationListener;
    }

    public void setOperationListener(AsyncOperationListener requestListener) {
        mOperationListener = requestListener;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public void start(Param param) {
        execute(param);
        sendBroadcast(ACTION_OPERATION_START);
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

    public void registerOperationReceiver(Context context, BroadcastReceiver receiver) {
        if (context != null && receiver != null && !receiver.isOrderedBroadcast()) {
            mContext = context;

            IntentFilter startIntentFilter = new IntentFilter(ACTION_OPERATION_START);
            IntentFilter finishIntentFilter = new IntentFilter(ACTION_OPERATION_FINISH);
            IntentFilter cancelIntentFilter = new IntentFilter(ACTION_OPERATION_CANCEL);

            context.registerReceiver(receiver, startIntentFilter);
            context.registerReceiver(receiver, finishIntentFilter);
            context.registerReceiver(receiver, cancelIntentFilter);
        }
    }

    public void unregisterOperationReceiver(Context context, BroadcastReceiver receiver) {
        if (context != null && receiver != null && receiver.isOrderedBroadcast())
            context.unregisterReceiver(receiver);
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

        sendBroadcast(ACTION_OPERATION_FINISH);
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

        sendBroadcast(ACTION_OPERATION_CANCEL);
    }

    private void sendBroadcast(String action) {
        if (mContext != null) {
            Intent intent = new Intent(action);
            intent.putExtra(EXTRA_OPERATION_ID, mId);
            mContext.sendBroadcast(intent);
        }
    }

    public interface AsyncOperationListener<Result> {
        public void onSuccess(Result result, AsyncOperation operation);
        public void onFailure(Exception exception, AsyncOperation operation);
        public void onProgressChanged(int progress, AsyncOperation operation);
        public void onCancelled(AsyncOperation operation);
    }
}

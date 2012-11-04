package com.pc.nettools.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.pc.nettools.AsyncOperation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Pietro Caselani
 */
public class AsyncClient {
    private static final int GET_METHOD = 10;
    private static final int POST_METHOD = 11;

    private String mBaseLink;
    private HashMap<String, String> mDefaultHeaders;
    private int mTimeout;
    private Set<AsyncHttpRequest> mRequests;
    private Context mContext;

    public AsyncClient(String baseLink, Context context) {
        mBaseLink = baseLink;
        mContext = context;
        mDefaultHeaders = new HashMap<String, String>();
        mRequests = new HashSet<AsyncHttpRequest>();
    }

    public AsyncClient(String baseLink) {
        this(baseLink, null);
    }

    public AsyncClient(Context context) {
        this(null, context);
    }

    public AsyncClient() {
        this(null, null);
    }

    public void setTimeout(int timeout) {
        mTimeout = timeout;
    }

    public int getTimeout() {
        return mTimeout;
    }

    public void addDefaultHeader(String header, String value) {
        mDefaultHeaders.put(header, value);
    }

    public void setDefaultAuthentication(String username, String password) {
        final String usernameConst = username, passwordConst = password;
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usernameConst, passwordConst.toCharArray());
            }
        });
    }

    public AsyncHttpRequest get(String path, HttpResponseHandler responseHandler) {
        return get(path, null, responseHandler);
    }

    public AsyncHttpRequest get(String path, HashMap<String, String> params, HttpResponseHandler responseHandler) {
        return configureRequest(path, params, GET_METHOD, responseHandler);
    }

    public AsyncHttpRequest post(String path, HttpResponseHandler responseHandler) {
        return post(path, null, responseHandler);
    }

    public AsyncHttpRequest post(String path, HashMap<String, String> params, HttpResponseHandler responseHandler) {
        return configureRequest(path, params, POST_METHOD, responseHandler);
    }

    public boolean cancelAllRequests() {
        boolean cancelled = true;

        while (mRequests.iterator().hasNext()) {
            AsyncHttpRequest request = mRequests.iterator().next();
            cancelled &= request.cancel();
        }

        return cancelled;
    }

    private AsyncHttpRequest configureRequest(String path, HashMap<String, String> params, int method, HttpResponseHandler responseHandler) {
        String link = mBaseLink == null ? path : mBaseLink.concat(path);
        String paramsString = null;

        int statusCode = -1;

        if (params != null) {
            try {
                paramsString = getParamsString(params);
                String specialChar = link.contains("?") ? "&" : "?";
                link = link.concat(specialChar).concat(paramsString);
            } catch (UnsupportedEncodingException e) {
                if (responseHandler != null)
                    responseHandler.onFailure(e, null, statusCode);
            }
        }

        try {
            URL url = new URL(link);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(getMethodName(method));
            connection.setReadTimeout(mTimeout);

            int size = mDefaultHeaders.size();
            String[] keys = new String[size];
            mDefaultHeaders.keySet().toArray(keys);

            for (String key : keys) {
                String value = mDefaultHeaders.get(key);
                connection.setRequestProperty(key, value);
            }

            if (method == POST_METHOD) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Accept-Charset", "UTF-8");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                if (paramsString != null) {
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(paramsString.getBytes("UTF-8"));
                }
            }

            AsyncHttpRequest asyncHttpRequest = new AsyncHttpRequest(responseHandler);

            if (mContext != null) {
                asyncHttpRequest.setId(mRequests.size() + 1);
                mRequests.add(asyncHttpRequest);
                asyncHttpRequest.registerOperationReceiver(mContext, new OperationStatusReceiver());
            }

            asyncHttpRequest.start(connection);

            return asyncHttpRequest;
        } catch (MalformedURLException e) {
            if (responseHandler != null)
                responseHandler.onFailure(e, null, statusCode);
        } catch (IOException e) {
            if (responseHandler != null)
                responseHandler.onFailure(e, null, statusCode);
        }

        return null;
    }

    private String getParamsString(HashMap<String, String> params) throws UnsupportedEncodingException {
        int size = params.size(), i = 0;
        String[] keys = new String[size];
        params.keySet().toArray(keys);
        size--;

        StringBuilder stringBuilder = new StringBuilder();

        for (String key : keys) {
            String value = URLEncoder.encode(params.get(key), "UTF-8");
            stringBuilder.append(key).append("=").append(value);
            if (i++ < size) stringBuilder.append("&");
        }

        return stringBuilder.toString();
    }

    private String getMethodName(int method) {
        switch (method) {
            case GET_METHOD:
                return "GET";
            case POST_METHOD:
                return "POST";
            default:
                return "GET";
        }
    }

    class OperationStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(AsyncOperation.ACTION_OPERATION_CANCEL) ||
                    intent.getAction().equalsIgnoreCase(AsyncOperation.ACTION_OPERATION_FINISH)) {
                int id = intent.getIntExtra(AsyncOperation.EXTRA_OPERATION_ID, -1);
                if (id > 0) {
                    for (AsyncHttpRequest request : mRequests) {
                        if (request.getId() == id) {
                            mRequests.remove(request);
                            break;
                        }
                    }
                }
            }
        }
    }
}
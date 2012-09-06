package com.pc.nettools.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.HashMap;

/**
 * Created by Pietro Caselani
 */
public class AsyncClient {
    private static final int GET_METHOD = 10;
    private static final int POST_METHOD = 11;

    private String mBaseLink;
    private HashMap<String, String> mDefaultHeaders;
    private int mTimeout;
    private HashMap<String, AsyncHttpRequest> mRequests;

    public AsyncClient(String baseLink) {
        mBaseLink = baseLink;
        mDefaultHeaders = new HashMap<String, String>();
        mRequests = new HashMap<String, AsyncHttpRequest>();
    }

    public AsyncClient() {
        this(null);
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
        String[] keys = new String[mRequests.size()];
        mRequests.keySet().toArray(keys);

        boolean cancelled = true;

        for (String key : keys) {
            AsyncHttpRequest request = mRequests.get(key);
            cancelled &= request.cancel();
        }

        return cancelled;
    }

    private AsyncHttpRequest configureRequest(String path, HashMap<String, String> params, int method, HttpResponseHandler responseHandler) {
        String link = mBaseLink == null ? path : mBaseLink.concat(path);
        String paramsString = null;

        if (params != null) {
            try {
                paramsString = getParamsString(params);
                String specialChar = link.contains("?") ? "&" : "?";
                link = link.concat(specialChar).concat(paramsString);
            } catch (UnsupportedEncodingException e) {
                if (responseHandler != null)
                    responseHandler.onFailure(e, null);
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
            asyncHttpRequest.start(connection);

            mRequests.put(link, asyncHttpRequest);

            return asyncHttpRequest;
        } catch (MalformedURLException e) {
            if (responseHandler != null)
                responseHandler.onFailure(e, null);
        } catch (IOException e) {
            if (responseHandler != null)
                responseHandler.onFailure(e, null);
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
}
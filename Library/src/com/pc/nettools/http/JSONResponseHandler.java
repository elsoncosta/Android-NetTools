package com.pc.nettools.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Pietro Caselani
 */
public class JSONResponseHandler extends HttpResponseHandler {
    private Object mJSON;

    public void onSuccess(Object json, AsyncHttpRequest request, int statusCode) {}

    @Override
    public void onFinish() {
        if (mJSON != null)
            onSuccess(mJSON, mRequest, mStatusCode);
        else
            onFailure(mException, mRequest, mStatusCode);
    }

    @Override
    public Object getResponseObject() {
        return mJSON;
    }

    @Override
    public void sendSuccessMessage(ByteArrayOutputStream outputStream) {
        String jsonString = outputStream.toString();

        if (jsonString == null) throw new IllegalStateException("jsonString can't be null");

        if (jsonString.startsWith("[") || jsonString.startsWith("{")) {
            try {
                Object json = new JSONTokener(jsonString).nextValue();
                mJSON = parse(json);
            } catch (JSONException e) {
                mException = e;
            }
        }
    }

    private Object parse(Object json) throws JSONException {
        if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;
            HashMap hashMap = new HashMap(jsonObject.length());

            Iterator iterator = jsonObject.keys();

            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                Object value = jsonObject.get(key);

                if (value instanceof JSONArray) value = parse(value);

                hashMap.put(key, value);
            }

            return hashMap;
        } else if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;
            ArrayList arrayList = new ArrayList(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                Object obj = jsonArray.get(i);

                if (obj instanceof JSONArray)
                    obj = parse(jsonArray);

                if (obj instanceof JSONObject)
                    obj = parse(obj);

                arrayList.add(obj);
            }

            return arrayList;
        }
        return null;
    }
}
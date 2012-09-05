package com.pc.nettools.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Pietro Caselani
 */
public class JSONResponseHandler extends HttpResponseHandler {
    private ArrayList mArray;
    private HashMap mMap;

    public void onSuccess(ArrayList json, AsyncHttpRequest request) {}
    public void onSuccess(HashMap json, AsyncHttpRequest request) {}

    @Override
    public void onFinish() {
        if (mArray != null)
            onSuccess(mArray, mRequest);
        else if (mMap != null)
            onSuccess(mMap, mRequest);
        else
            onFailure(mException, mRequest);
    }

    @Override
    public void sendSuccessMessage(ByteArrayOutputStream outputStream) {
        String jsonString = outputStream.toString();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            mMap = (HashMap) parse(jsonObject);
        } catch (JSONException e) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                mArray = (ArrayList) parse(jsonArray);
            } catch (JSONException e1) {
                mException = e1;
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
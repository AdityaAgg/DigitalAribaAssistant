package com.ibm.watson.apis.conversation_with_nlu.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesResult;
import okhttp3.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by i862250 on 13/6/17.
 */
public class UtilMethods {
    public static String extractEntity(String entityName, AnalysisResults results) {
        if (results != null && results.getEntities() != null)
            for (EntitiesResult entitiesResult : results.getEntities()) {
                if (entitiesResult.getType().equals(entityName)) {
                    return entitiesResult.getText();
                }
            }
        return null;
    }


    public static Map<String, String> extractEntities(AnalysisResults results) {
        Map<String, String> entitiesMap = new HashMap<>();

        if (results != null && results.getEntities() != null) {
            for (EntitiesResult entitiesResult : results.getEntities())
                entitiesMap.put(entitiesResult.getType(), entitiesResult.getText());
            return entitiesMap;
        }
        return null;
    }


    public static JsonObject parsetoJsonObject(String json) {
        JsonParser parser = new JsonParser();
        return parser.parse(json).getAsJsonObject();
    }

    public static JsonArray parsetoJsonArray(String json) {
        JsonParser parser = new JsonParser();
        return parser.parse(json).getAsJsonArray();
    }


    public static JsonObject generateJsonObject(List<String> attriubutes) {
        JsonObject jsonObject = new JsonObject();
        for (String attribute : attriubutes)
            jsonObject.addProperty(attribute, "");
        return jsonObject;
    }


    public static Request requestBuilder(String url, TreeMap<String, String> parameters, Headers headers, String body, int method, MediaType type) {
        Request request;
        Request.Builder requestBuilder = new Request.Builder();

        if (headers != null)
            requestBuilder.headers(headers);
        requestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
        switch (method) {
            case Constants.GET:
                if (parameters != null)
                    url = UtilMethods.urlProcessor(url, parameters);
                request = requestBuilder.url(url).build();

                break;
            case Constants.POST:
            case Constants.PUT:

                if (body != null) {
                    RequestBody requestBody = RequestBody.create(type, body);
                    requestBuilder = requestBuilder.post(requestBody);
                }
                request = requestBuilder.url(url).build();


                break;
            case Constants.DELETE:
                request = new Request.Builder().url(url).build();
                break;
            default:
                request = null;
                break;
        }
        return request;
    }

    public static String urlProcessor(String baseUrl, TreeMap<String, String> parameters) {
        if (parameters != null && parameters.size() > 0) {
            baseUrl += '?';


            Iterator entryIterator = parameters.entrySet().iterator();

            while (entryIterator.hasNext()) {
                Map.Entry pair = (Map.Entry) entryIterator.next();
                String keyValue = null;
                try {
                    keyValue = URLEncoder.encode(pair.getKey().toString(), "UTF-8") + "=" + URLEncoder.encode(pair.getValue().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } finally {
                    if (keyValue != null)
                        baseUrl += keyValue;
                }
                entryIterator.remove(); // avoids a ConcurrentModificationException
                if (entryIterator.hasNext())
                    baseUrl += "&";

            }
        }
        return baseUrl;
    }

}

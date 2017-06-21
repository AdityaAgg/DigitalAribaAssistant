package com.ibm.watson.apis.conversation_with_discovery.utils;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesResult;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

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

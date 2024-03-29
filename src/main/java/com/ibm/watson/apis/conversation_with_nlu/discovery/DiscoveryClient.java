/*
 * Copyright 2015 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.ibm.watson.apis.conversation_with_nlu.discovery;

import com.google.gson.*;
import com.ibm.watson.apis.conversation_with_nlu.payload.DocumentPayload;
import com.ibm.watson.apis.conversation_with_nlu.payload.Payload;
import com.ibm.watson.apis.conversation_with_nlu.utils.Constants;
import com.ibm.watson.apis.conversation_with_nlu.utils.Messages;
import com.ibm.watson.developer_cloud.discovery.v1.model.query.Aggregation;
import com.ibm.watson.developer_cloud.discovery.v1.model.query.QueryResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DiscoveryClient.
 */
public class DiscoveryClient {

    private static final Logger logger = LogManager.getLogger(DiscoveryClient.class.getName());

    private static final int SNIPPET_LENGTH = 150;

    private Pattern pattern = Pattern.compile("((.+?)</p>){1,2}");

    /**
     * This method uses the Query object to send the user's query (the <code>input</code> param) to the discovery service.
     *
     * @param input The user's query to be sent to the discovery service
     * @return A list of DocumentPayload objects, each representing a single document the discovery service believes is a
     * possible answer to the user's query
     * @throws Exception the exception
     */
    public Payload getDocuments(String input, String aggregation) throws Exception {
        DiscoveryQuery discoveryQuery = new DiscoveryQuery();
        QueryResponse output = discoveryQuery.query(input, aggregation);
        List<Map<String, Object>> results = output.getResults();
        Aggregation sentimentAggregation = output.getAggregations().get(0);
        String aggJsonRes = new Gson().toJson(sentimentAggregation);

        String jsonRes = new Gson().toJson(results);
        JsonElement jelementAgg = new JsonParser().parse(aggJsonRes);
        JsonElement jelement = new JsonParser().parse(jsonRes);

        return createPayload(jelement, jelementAgg);
    }

    /**
     * Helper Method to include highlighting information along with the Discovery response so the final payload
     * includes id,title,body,sourceUrl as json key value pairs.
     *
     * @param resultsElement the results element
     * @return A list of DocumentPayload objects, each representing a single document the discovery service believes is a
     * possible answer to the user's query
     */
    private Payload createPayload(JsonElement resultsElement, JsonElement aggElement) {
        logger.info(Messages.getString("Service.CREATING_DISCOVERY_PAYLOAD"));
        List<DocumentPayload> payload = new ArrayList<DocumentPayload>();
        JsonArray jarray = resultsElement.getAsJsonArray();


        if (jarray.size() > 0) {
            for (int i = 0; (i < jarray.size()) && (i < Constants.DISCOVERY_MAX_SEARCH_RESULTS_TO_SHOW); i++) {
                DocumentPayload documentPayload = new DocumentPayload();
                String id = jarray.get(i).getAsJsonObject().get(Constants.DISCOVERY_FIELD_ID).toString().replaceAll("\"", "");
                documentPayload.setId(id);
                documentPayload.setTitle(
                        jarray.get(i).getAsJsonObject().get(Constants.DISCOVERY_FIELD_TITLE).toString().replaceAll("\"", ""));
                if (jarray.get(i).getAsJsonObject().get(Constants.DISCOVERY_FIELD_BODY) != null) {
                    String body = jarray.get(i).getAsJsonObject().get(Constants.DISCOVERY_FIELD_BODY).toString().replaceAll("\"",
                            "");

                    // This method limits the response text in this sample
                    // app to two paragraphs.
                    String bodyTwoPara = limitParagraph(body);
                    documentPayload.setBody(bodyTwoPara);
                    documentPayload.setBodySnippet(getSniplet(body));

                } else {
                    documentPayload.setBody("empty");
                }
                if (jarray.get(i).getAsJsonObject().get(Constants.DISCOVERY_FIELD_SOURCE_URL) == null) {
                    documentPayload.setSourceUrl("empty");
                } else {
                    documentPayload.setSourceUrl(jarray.get(i).getAsJsonObject().get(Constants.DISCOVERY_FIELD_SOURCE_URL)
                            .toString().replaceAll("\"", ""));
                }
                if (jarray.get(i).getAsJsonObject().get(Constants.DISCOVERY_FIELD_CONFIDENCE) != null) {
                    documentPayload.setConfidence(jarray.get(i).getAsJsonObject().get(Constants.DISCOVERY_FIELD_CONFIDENCE)
                            .toString().replaceAll("\"", ""));
                } else {
                    documentPayload.setConfidence("0.0");
                }

                if(jarray.get(i).getAsJsonObject().get(Constants.DISCOVERY_FIELD_DATE) == null) {
                    documentPayload.setDate(null);
                } else {
                    String dateString = jarray.get(i).getAsJsonObject().get(Constants.DISCOVERY_FIELD_DATE).getAsString();
                    DateFormat df = new SimpleDateFormat("yyyyMMdd");

                    try {
                        Date newDate = df.parse(jarray.get(i).getAsJsonObject().get(Constants.DISCOVERY_FIELD_DATE).getAsString());
                        documentPayload.setDate(newDate);
                    } catch (ParseException e) {
                        documentPayload.setDate(null);
                        e.printStackTrace();
                    }
                }
                if(jarray.get(i).getAsJsonObject().get(Constants.DISCOVERY_FIELD_SOURCE)==null) {
                    documentPayload.setSource(null);
                } else {
                    documentPayload.setSource(jarray.get(i).getAsJsonObject().get(Constants.DISCOVERY_FIELD_SOURCE).getAsString().substring(4));
                }

                documentPayload.setType("news");


                payload.add(i, documentPayload);
            }
        }

        JsonArray aggregationResults = aggElement.getAsJsonObject().get("results").getAsJsonArray();
       // aggregationResults = aggregationResults.get("elements").getAsJsonArray();
        Map<String, Double> sentimentMap = new HashMap<>();
        double sumSentiment = 0;
        for (int i = 0; i < aggregationResults.size(); ++i) {

            JsonObject sentiment = aggregationResults.get(i).getAsJsonObject();
            sumSentiment += sentiment.get("matching_results").getAsDouble();

            sentimentMap.put(sentiment.get("key").getAsString(), sentiment.get("matching_results").getAsDouble());
        }
        Iterator it = sentimentMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            sentimentMap.put(pair.getKey().toString(), (double) pair.getValue() / sumSentiment);

        }
        Payload completePayload = new Payload(payload, sentimentMap);


        return completePayload;
    }

    /**
     * get first <code>SNIPPET_LENGTH</code> characters of body response.
     *
     * @param body discovery response
     * @return
     */
    private String getSniplet(String body) {
        if (body == null) {
            return "";
        }

        int len = body.length();
        if (len > SNIPPET_LENGTH) {
            body = body.substring(0, SNIPPET_LENGTH - 3) + "...";
        }
        return body;
    }

    /**
     * This method limits the response text in this sample app to two paragraphs. For your own application, you can
     * comment out the method to allow the full text to be returned.
     *
     * @param body
     * @return string
     */
    private String limitParagraph(String body) {
        String returnString = body;

        Matcher matcher = pattern.matcher(body);
        if (matcher.find()) {
            returnString = matcher.group(0);
        }

        return returnString;
    }
}

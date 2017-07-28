package com.ibm.watson.apis.conversation_with_nlu.payload;

import java.util.List;
import java.util.Map;

/**
 * Created by i862250 on 16/7/17.
 */
public class Payload {


    public Payload(List<DocumentPayload> payload, Map<String, Double> sentimentList) {
        setPayloadList(payload);
        setSentiment(sentimentList);
    }

    public void setPayloadList(List<DocumentPayload> payloadList) {
        this.payloadList = payloadList;
    }

    private List<DocumentPayload> payloadList;

    public List<DocumentPayload> getPayloadList() {
        return payloadList;
    }

    public Map<String, Double> getSentiment() {
        return sentiment;
    }

    private Map<String, Double> sentiment;
    public void setSentiment(Map<String, Double> sentimentMap) {
        sentiment = sentimentMap;
    }



}

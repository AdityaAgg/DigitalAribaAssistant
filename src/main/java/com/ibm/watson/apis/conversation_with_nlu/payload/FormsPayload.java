package com.ibm.watson.apis.conversation_with_nlu.payload;

public class FormsPayload {
    String origin;
    String destination;
    String requestor;

    String product;
    String id;

    public FormsPayload(String origin, String destination, String product, double cost, String id, String userId) {
        this.origin = origin;
        this.destination = destination;
        this.product = product;
        this.cost = cost;
        this.id = id;
        this.requestor = userId;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    double cost;

}

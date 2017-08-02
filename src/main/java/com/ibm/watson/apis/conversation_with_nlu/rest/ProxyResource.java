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

package com.ibm.watson.apis.conversation_with_nlu.rest;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.ibm.watson.apis.conversation_with_nlu.discovery.DiscoveryClient;
import com.ibm.watson.apis.conversation_with_nlu.nlu.NluClient;
import com.ibm.watson.apis.conversation_with_nlu.payload.DocumentPayload;
import com.ibm.watson.apis.conversation_with_nlu.payload.FormsPayload;
import com.ibm.watson.apis.conversation_with_nlu.payload.Payload;
import com.ibm.watson.apis.conversation_with_nlu.utils.Constants;
import com.ibm.watson.apis.conversation_with_nlu.utils.Messages;
import com.ibm.watson.apis.conversation_with_nlu.utils.UtilMethods;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.service.exception.UnauthorizedException;
import com.ibm.watson.developer_cloud.util.GsonSingleton;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.net.www.http.HttpClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Class ProxyResource.
 */
@Path("conversation/api/v1/workspaces")
public class ProxyResource {
    private static String API_VERSION = "2017-05-26";


    private static final String ERROR = "error";
    private static final Logger logger = LogManager.getLogger(ProxyResource.class.getName());

    private DiscoveryClient discoveryClient = new DiscoveryClient();
    private String password = System.getenv("CONVERSATION_PASSWORD");

    private String url;

    private String username = System.getenv("CONVERSATION_USERNAME");
    private OkHttpClient httpClient = new OkHttpClient();
    private JsonArray suppliers;

    private MessageRequest buildMessageFromPayload(InputStream body) {
        StringBuilder sbuilder = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(body, "UTF-8"));
            sbuilder = new StringBuilder();
            String str = reader.readLine();
            while (str != null) {
                sbuilder.append(str);
                str = reader.readLine();
                if (str != null) {
                    sbuilder.append("\n");
                }
            }
            return GsonSingleton.getGson().fromJson(sbuilder.toString(), MessageRequest.class);
        } catch (IOException e) {
            logger.error(Messages.getString("ProxyResource.JSON_READ"), e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.error(Messages.getString("ProxyResource.STREAM_CLOSE"), e);
            }
        }
        return null;
    }

    /**
     * This method is responsible for sending the query the user types into the UI to the Watson services. The code
     * demonstrates how the conversation service is called, how the response is evaluated, and how the response is then
     * sent to the nlu service if necessary.
     *
     * @param request The full query the user asked of Watson
     * @param id      The ID of the conversational workspace
     * @return The response from Watson. The response will always contain the conversation service's response. If the
     * intent confidence is high or the intent is out_of_scope, the response will also contain information from
     * the nlu service
     */
    private MessageResponse getWatsonResponse(MessageRequest request, String id) throws Exception {

        // Configure the Watson Developer Cloud SDK to make a call to the
        // appropriate conversation service.

        ConversationService service =
                new ConversationService(API_VERSION);
        if ((username != null) || (password != null)) {
            service.setUsernameAndPassword(username, password);
        }

        service.setEndPoint(url == null ? Constants.CONVERSATION_URL : url);

        // Use the previously configured service object to make a call to the
        // conversational service
        MessageResponse response = service.message(id, request).execute();


        if (response.getOutput() != null && response.getOutput().containsKey("action")) {
            Map<String, Object> context = response.getContext();
            if (context == null) {
                context = new HashMap<>();
                response.setContext(context);
            }
            evaluateAction((String) response.getOutput().get("action"), response, request);
        }


        return response;
    }

    /**
     * check if document reference exists in context
     *
     * @param docReference
     * @param context
     * @return
     */
    private String documentRefExists(String docReference, Map<String, Object> context) {
        return (String) context.get("doc_" + docReference.charAt(docReference.length() - 1));
    }


    private void evaluateAction(String action, MessageResponse messageResponse, MessageRequest messageRequest) throws IOException {

        Request request = null;
        okhttp3.Response response = null;
        Headers headers = new Headers.Builder().add("Authorization", Constants.ACCESS_TOKEN).build();
        NluClient nluClient = new NluClient();
        Map<String, Object> context = messageResponse.getContext();
        switch (action) {
            case "past_forms":
                request = UtilMethods.requestBuilder("http://localhost:9090/Form/api/v1.0/document", null, headers, null, Constants.GET, Constants.JSON);
                response = httpClient.newCall(request).execute();
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code: " + response);
                else {
                    JsonParser parser = new JsonParser();
                    JsonArray recentDocuments = parser.parse(response.body().string()).getAsJsonArray();



                    int retrieveDocsAmount = 3;
                    if (recentDocuments.size() < 3)
                        retrieveDocsAmount = recentDocuments.size();
                    ArrayList<FormsPayload> formsPayloads = new ArrayList<>();
                    for (int i = 0; i < retrieveDocsAmount; ++i) {
                        JsonObject recentDoc = recentDocuments.get(i).getAsJsonObject();
                        String id = recentDoc.get("id").getAsString();
                        String requestor = recentDoc.get("userId").getAsString();
                        JsonObject data = recentDoc.get("data").getAsJsonObject();
                        String origin = data.get("5374310d-2a5f-4ade-79fd-1317db1d2b4a").getAsJsonObject().get("value").getAsString();
                        String destination = data.get("e89cab56-b090-ffba-063a-79181374617c").getAsJsonObject().get("value").getAsString();
                        String product = data.get("24645f6b-f905-a7fd-4ca8-c2e9888665a7").getAsJsonObject().get("value").getAsString();
                        Double cost = Double.parseDouble(data.get("66c89f43-6c11-2a43-86bc-d0d9d015de17").getAsJsonObject().get("value").getAsString());

                        FormsPayload formsPayload = new FormsPayload(origin, destination, product, cost, id, requestor);
                        formsPayloads.add(formsPayload);


                        context.put("doc_" + (i + 1), id);

                    }

                    Map<String, Object> output = messageResponse.getOutput();
                    String json = new Gson().toJson(formsPayloads);
                    context.put("recent_docs", json);
                    String[] texts = new String[1];
                    texts[0] = "Your past forms are: ";
                    output.put("text", texts);
                    output.put("past_forms", true);
                }
                response.body().close(); //release response resources



                /* ****
                for switching to ansynchronous architecture later down the line

                httpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {

                    }
                });*/

                break;
            case "create_form":
                try {

                    AnalysisResults results = nluClient.query(messageRequest.inputText());
                    Map<String, String> createFromFormEntities = UtilMethods.extractEntities(results);
                    String createFormValue;
                    createFormValue = createFromFormEntities.get("form_id");

                    context.put("form_population_success", false);

                    if (createFormValue == null) {
                        createFormValue = createFromFormEntities.get("doc_ref_id");
                        if (createFormValue != null)
                            createFormValue = documentRefExists(createFormValue, context);
                    }
                    System.out.println("Create Form ID value " + createFormValue);

                    if (createFormValue == null)  //we'll assume if we could not extract the form name the user meant the form selected -- in context
                        createFormValue = (String) context.get("selected_form");


                    if (createFormValue == null || createFormValue.toLowerCase().trim().contains("doc")) {
                        String[] texts = new String[1];
                        texts[0] = "Could not find the form you are looking for. Sorry, failed to get data from form.";//TODO: send failed requests to a Watson service for a human reviewer to make the bot more intelligent?

                        Map<String, Object> output = messageResponse.getOutput();
                        output.put("text", texts);

                    } else {

                        //Run logic to return data from already created document
                        request = UtilMethods.requestBuilder("http://localhost:9090/Form/api/v1.0/documents/" + createFormValue, null, headers, null, Constants.GET, Constants.JSON);
                        response = httpClient.newCall(request).execute();

                        if (!response.isSuccessful()) //error in response -- response not ideal
                            throw new IOException("Unexpected code: " + response);
                        else {
                            JsonParser parser = new JsonParser();
                            JsonObject document = parser.parse(response.body().string()).getAsJsonObject();
                            if (document.get("status").equals("404")) {
                                String[] texts = new String[1];
                                texts[0] = "Could not find the form you are looking for. Sorry, failed to get data from form.";//TODO: send failed requests to a Watson service for a human reviewer to make the bot more intelligent?
                                Map<String, Object> output = messageResponse.getOutput();
                                output.put("text", texts);
                            } else {

                                String stringData = document.get("data").toString();

                                Map<String, Object> output = messageResponse.getOutput();
                                String[] texts = new String[1];
                                texts[0] = " Stored in context variable create_from_data " + stringData + "<br> Now I can assist you with supplier selection.";

                                output.put("text", texts);
                                context.put("create_from_data", stringData);
                                context.put("form_population_success", true);
                            }
                        }
                        response.body().close();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case "select_form":


                Map<String, String> extractedEntities = new HashMap<>();
                try {
                    extractedEntities = UtilMethods.extractEntities(nluClient.query(messageRequest.inputText()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String selectedForm = extractedEntities.get("form_id");
                if (selectedForm != null) {
                    context.put("selected_form", selectedForm); //stores form id instead of reference (Document Number)
                } else {
                    selectedForm = extractedEntities.get("doc_ref_id");
                    if (selectedForm != null)
                        context.put("selected_form", documentRefExists(selectedForm, context));
                }


                break;
            case "delivery_time_inquiry":
                deliveryTimeAction(messageResponse.getContext(), httpClient, messageResponse);
                break;
            case "more_info_delay":
                moreInfoDelay(messageResponse.getContext(), null, messageResponse);
                break;
            case "budget_amt_check":
                context.put("current_product", "Electric Turbine Blade");
                context.put("current_supplier", "Aero Brand");
                String productName = (String) context.get("current_product");
                String supplierName = (String) context.get("current_supplier");


                if (suppliers == null) setSuppliers(context, productName);

                String[] texts = new String[1];

                for (int i = 0; i < suppliers.size(); ++i) {
                    JsonObject supplier = suppliers.get(i).getAsJsonObject();
                    String supplierNameString = supplier.get("name").getAsString();
                    if (supplierNameString.trim().toLowerCase().equals(supplierName.trim().toLowerCase())) {
                        double cost = supplier.get("cost").getAsDouble();
                        double balance = Constants.BUDGET - cost;
                        if (balance < 0) {
                            texts[0] = "You are $" + Math.abs(balance) + " under budget.";
                        } else {
                            if (!(Boolean) context.get("supplierSelection"))
                                texts[0] = "You have a $" + balance + " surplus.";
                        }
                    }
                }
                Map<String, Object> output = messageResponse.getOutput();
                output.put("text", texts);
                break;

            case "other_supplier_opt":
                context.put("current_product", "Electric Turbine Blade");
                context.put("current_supplier", "Aero Brand");
                productName = (String) context.get("current_product");
                supplierName = (String) context.get("current_supplier");


                if (suppliers == null) setSuppliers(context, productName);

                texts = new String[1];
                texts[0] = "Here are some other options:";
                /*for (int i = 0; i < suppliers.size(); ++i) {

                    JsonObject supplier = suppliers.get(i).getAsJsonObject();
                    if (!supplier.get("name").getAsString().equals(supplierName)) {
                        double supplierCost = supplier.get("cost").getAsDouble();
                        texts[0] += "<li> " + supplier.get("name").getAsString() + " (costs: $" + supplierCost + ") ( $" + (Constants.BUDGET - supplierCost) + ") </li>";
                    }
                }*/

                output = messageResponse.getOutput();
                output.put("text", texts);
                output.put("other_supplier_options", true);


                break;
            default:
                break;
        }

    }

    private void setSuppliers(Map<String, Object> context, String productName) {
        if (context.get("suppliers") == null) {
            Request request = UtilMethods.requestBuilder("https://supplier-provider.mybluemix.net/api/suppliers?product=" + productName, null, null, null, Constants.GET, Constants.JSON);
            okhttp3.Response response = null;
            try {
                response = httpClient.newCall(request).execute();
                suppliers = new JsonParser().parse(response.body().string()).getAsJsonArray();
            } catch (IOException e) {
                e.printStackTrace();
            }

            context.put("suppliers", suppliers);
        } else {
            String json = new Gson().toJson(context.get("suppliers"));
            suppliers = new JsonParser().parse(json).getAsJsonArray();
        }

    }


    private void moreInfoDelay(Map<String, Object> context, HttpClient httpClient, MessageResponse response) {
        ArrayList<LinkedTreeMap<String, Object>> delays = (ArrayList) context.get("delays");
        boolean returnValue = false;
        if (delays != null && delays.size() > 0) {
            returnValue = true;
        }
        response.getOutput().put("delay", returnValue);


    }

    private void deliveryTimeAction(Map<String, Object> context, OkHttpClient httpClient, MessageResponse response) {
        boolean delayDuetoNews = newsAction(context, "Doha");
        //TODO: get origin data from angular application
        //boolean delayDuetoWeather = weatherAction(context, httpClient, "Los Angeles, California");

        String[] texts = new String[1];
        Map<String, Object> output = response.getOutput();
        if (delayDuetoNews) { //|| delayDuetoWeather
            texts[0] = "This supplier is on time, however, there could be a delay due";
        } else {

            texts[0] = "Seems like there's a high likelihood that you will receive your shipment on time.";
            output.put("text", texts);
        }
        /*
        if (delayDuetoWeather) {
            texts[0] += ", to bad weather forecast";
            output.put("text", texts);
        }*/

        if (delayDuetoNews) {
            texts[0] += ", to reasons detailed in recent shipping news releases";
        }
        output.put("text", texts);


    }

    private boolean weatherAction(Map<String, Object> context, OkHttpClient httpClient, String query) {
        String VCAP_STRING = System.getenv("VCAP_SERVICES");
        String username;
        String password;
        if (VCAP_STRING != null) {
            JsonObject VCAP_SERVICES = new JsonParser().parse(System.getenv("VCAP_SERVICES")).getAsJsonObject();

            JsonObject credentials = VCAP_SERVICES.get("weatherinsights").getAsJsonArray().get(0).getAsJsonObject().get("credentials").getAsJsonObject();

            username = credentials.get("username").getAsString();
            password = credentials.get("password").getAsString();
        } else {
            username = "ab7dc1ff-5fb0-4592-8e58-ecb39370de9a";
            password = "WqieJBfSBi";
        }

        String weatherUrl = "https://twcservice.mybluemix.net:443/api/weather";
        TreeMap<String, String> parameters = new TreeMap<>();
        parameters.put("query", query);
        parameters.put("language", "en-US");

        Headers headers = new Headers.Builder().add("Content-Type", "application/json;charset=utf-8").add("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes())).add("Accept", "application/json").build();

        Request request = UtilMethods.requestBuilder(weatherUrl + "/v3/location/search", parameters, headers, null, Constants.GET, null);

        try {
            okhttp3.Response locationResponse = httpClient.newCall(request).execute();

            JsonObject locationData = UtilMethods.parsetoJsonObject(locationResponse.body().string());
            locationData = locationData.get("location").getAsJsonObject();
            JsonElement longtitude = locationData.get("longitude");
            JsonElement latitude = locationData.get("latitude");
            double longtitudeDouble;
            double latitudeDouble;

            if (longtitude.isJsonArray())
                longtitudeDouble = longtitude.getAsJsonArray().get(0).getAsDouble();
            else
                longtitudeDouble = longtitude.getAsDouble();

            if (latitude.isJsonArray())
                latitudeDouble = latitude.getAsJsonArray().get(0).getAsDouble();
            else
                latitudeDouble = latitude.getAsDouble();


            locationResponse.body().close();
            parameters = new TreeMap<>();
            parameters.put("language", "en-US");
            Request alertsRequest = UtilMethods.requestBuilder(weatherUrl + "/v1/geocode/" + latitudeDouble + "/" + longtitudeDouble + "/alerts.json", parameters, headers, null, Constants.GET, null); //Note that this works only for Europe, USA, and Canada
            okhttp3.Response alertsResponse = httpClient.newCall(alertsRequest).execute();
            JsonObject alertsObject = UtilMethods.parsetoJsonObject(alertsResponse.body().string());
            if (!alertsObject.get("success").getAsBoolean())
                return false;

            JsonArray alertsArray = alertsObject.get("alerts").getAsJsonArray();


            String alertKey = "";
            boolean isRelevant;
            for (int i = 0; i < alertsArray.size(); ++i) {
                JsonObject alert = alertsArray.get(i).getAsJsonObject();
                alertKey = alert.get("key").getAsString();
                int severityCode = alert.get("severity_cd").getAsInt();
                JsonArray responseTypes = alert.get("response_types").getAsJsonArray();
                int urgencyCode = 5;
                int certaintyCode = 5;

                if (responseTypes.get(0) != null) {
                    JsonObject firstResponseType = responseTypes.get(0).getAsJsonObject();


                    if (firstResponseType.get("urgency_cd") != null) {
                        urgencyCode = firstResponseType.get("urgency_cd").getAsInt();
                    }


                    if (firstResponseType.get("certainty_cd") != null) {
                        certaintyCode = firstResponseType.get("certainty_cd").getAsInt();
                    }
                }

                Request alertsDetail = UtilMethods.requestBuilder(weatherUrl + "/v1/alert/" + alertKey + "/details.json", parameters, headers, null, Constants.GET, null);
                okhttp3.Response alertsDetailResponse = httpClient.newCall(alertsDetail).execute();
                JsonParser parser = new JsonParser();
                JsonObject alertsDetailObj = parser.parse(alertsDetailResponse.body().string()).getAsJsonObject();
                String description = null;


                JsonElement alertsDetailObjElement = alertsDetailObj.get("alertDetail");
                if (alertsDetailObjElement != null) {
                    alertsDetailObj = alertsDetailObjElement.getAsJsonObject();
                    JsonElement alertDetailsTextElement = alertsDetailObj.get("texts");
                    if (alertDetailsTextElement != null) {
                        JsonArray alertsDetailTexts = alertDetailsTextElement.getAsJsonArray();
                        JsonElement alertTextElement = alertsDetailTexts.get(0);
                        if (alertTextElement != null) {
                            JsonObject alertText = alertTextElement.getAsJsonObject();
                            JsonElement descriptionElement = alertText.get("description");
                            if (descriptionElement != null)
                                description = descriptionElement.getAsString();

                        }
                    }
                }
                alertsDetailResponse.body().close();


                isRelevant = (severityCode <= 3) && (urgencyCode != 4) && (certaintyCode != 4);
                if (isRelevant) {
                    JsonArray delays = new JsonArray();

                    String weatherHeadline = alert.get("headline_text").getAsString();
                    String source = alert.get("source").getAsString() + alert.get("office_name").getAsString() + ", " + alert.get("office_cntry_cd");
                    if (description == null)
                        description = "";
                    else
                        description = description.toLowerCase();//TODO: DocumentPayload for weather - sourceUrl and date fields missing
                    DocumentPayload documentPayload = new DocumentPayload(weatherHeadline, "weather", null, description.toLowerCase(), source, null);
                    Gson gson = new Gson();
                    delays.add(new JsonParser().parse(gson.toJson(documentPayload)).getAsJsonObject());


                    context.put("delays", delays);


                    return true;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean newsAction(Map<String, Object> context, String queryCity) {


        String query = "entities:(text:\"" + queryCity + "\",type::City),keywords.text:shipping";
        String aggregation = "term(docSentiment.type)";

        // Send the user's question to the discovery service
        Payload payload = null;
        try {
            payload = discoveryClient.getDocuments(query, aggregation);

            Map<String, Double> sentimentMap = payload.getSentiment();
            if (sentimentMap.get("negative") > 0.6) {
                if (context.get("delays") == null) {
                    context.put("delays", new JsonArray());
                }
                JsonArray delays = (JsonArray) context.get("delays");
                JsonArray newsAlerts = new JsonParser().parse(new Gson().toJson(payload.getPayloadList())).getAsJsonArray();
                delays.addAll(newsAlerts);
                return true;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }


    /**
     * Post message.
     *
     * @param id   the id
     * @param body the body
     * @return the response
     */
    @POST
    @Path("{id}/message")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postMessage(@PathParam("id") String id, InputStream body) {

        HashMap<String, Object> errorsOutput = new HashMap<String, Object>();
        MessageRequest request = buildMessageFromPayload(body);

        if (request == null) {
            throw new IllegalArgumentException(Messages.getString("ProxyResource.NO_REQUEST"));
        }

        MessageResponse response = null;

        try {
            response = getWatsonResponse(request, id);

        } catch (Exception e) {
            if (e instanceof UnauthorizedException) {
                errorsOutput.put(ERROR, Messages.getString("ProxyResource.INVALID_CONVERSATION_CREDS"));
            } else if (e instanceof IllegalArgumentException) {
                errorsOutput.put(ERROR, e.getMessage());
            } else if (e instanceof MalformedURLException) {
                errorsOutput.put(ERROR, Messages.getString("ProxyResource.MALFORMED_URL"));
            } else if (e.getMessage().contains("URL workspaceid parameter is not a valid GUID.")) {
                errorsOutput.put(ERROR, Messages.getString("ProxyResource.INVALID_WORKSPACEID"));
            } else {
                errorsOutput.put(ERROR, Messages.getString("ProxyResource.GENERIC_ERROR"));
            }

            logger.error(Messages.getString("ProxyResource.QUERY_EXCEPTION") + e.getMessage());
            return Response.ok(new Gson().toJson(errorsOutput, HashMap.class)).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(new Gson().toJson(response, MessageResponse.class)).type(MediaType.APPLICATION_JSON).build();
    }

    /**
     * Sets the conversation API version.
     *
     * @param version the new conversation API version
     */
    public static void setConversationAPIVersion(String version) {
        API_VERSION = version;
    }

    /**
     * Sets the credentials.
     *
     * @param username the username
     * @param password the password
     * @param url      the url
     */
    public void setCredentials(String username, String password, String url) {
        this.username = username;
        this.password = password;
        this.url = url;
    }
}

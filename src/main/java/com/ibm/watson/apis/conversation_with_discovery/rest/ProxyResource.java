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

package com.ibm.watson.apis.conversation_with_discovery.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.apis.conversation_with_discovery.nlu.NluClient;
import com.ibm.watson.apis.conversation_with_discovery.utils.Constants;
import com.ibm.watson.apis.conversation_with_discovery.utils.Messages;
import com.ibm.watson.apis.conversation_with_discovery.utils.UtilMethods;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.Entity;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.service.exception.UnauthorizedException;
import com.ibm.watson.developer_cloud.util.GsonSingleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Class ProxyResource.
 */
@Path("conversation/api/v1/workspaces")
public class ProxyResource {
    private static String API_VERSION = "2017-05-26";
    private static final String TMDB_URL = "http://api.themoviedb.org/3/search/movie";

    private static final String ERROR = "error";
    private static final Logger logger = LogManager.getLogger(ProxyResource.class.getName());


    private String password = System.getenv("CONVERSATION_PASSWORD");

    private String url;

    private String username = System.getenv("CONVERSATION_USERNAME");

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

        //TODO: Remove old code from discovery service when there is no more need to reference

        // Determine if conversation's response is sufficient to answer the
        // user's question or if we
        // should call the nlu service to obtain better answers

    /*if (response.getOutput().containsKey("action")
        && (response.getOutput().get("action").toString().indexOf("call_discovery") != -1)) {
      String query = response.getInputText();*/

        // Extract the user's original query from the conversational
        // response
     /* if ((query != null) && !query.isEmpty()) {

        // For this app, both the original conversation response and the
        // nlu response
        // are sent to the UI. Extract and add the conversational
        // response to the ultimate response
        // we will send to the user. The UI will process this response
        // and show the top 3 retrieve
        // and rank answers to the user in the main UI. The JSON
        // response section of the UI will
        // show information from the calls to both services.
        Map<String, Object> output = response.getOutput();
        if (output == null) {
          output = new HashMap<String, Object>();
          response.setOutput(output);
        }

        // Send the user's question to the nlu service
        List<DocumentPayload> docs = nluClient.getDocuments(query);

        // Append the nlu answers to the output object that will
        // be sent to the UI
        output.put("CEPayload", docs);
      }
    }*/

        if (response.getOutput() != null && response.getOutput().containsKey("action") && (response.getOutput().get("action").toString().indexOf("describe_movie")) != -1) {
            Map<String, Object> context = response.getContext();
            if (context == null) {
                context = new HashMap<>();
                response.setContext(context);
            }
            TreeMap<String, String> parameters = new TreeMap<>();
            parameters.put("api_key", "5933db740319ccfe5d332d4cbbd03f32");


            if (response.getEntities().size() == 0) {
                NluClient nluClient = new NluClient();

                String movieValue = UtilMethods.extractEntity("movie", nluClient.query(request.inputText()));

                //add movie entity to conversation workspace
                Integer[] locations = new Integer[2];
                locations[0] = request.inputText().indexOf(movieValue);
                locations[1] = locations[0] + movieValue.length();
                request = request.newBuilder().entity(new Entity("movie", movieValue, locations)).inputText(request.inputText()).build();
                service.message(id, request).execute();

                parameters.put("query", movieValue); //add to query parameters for api call to TMDB

            } else
                parameters.put("query", response.getEntities().get(0).getValue());

            JsonObject responseJson = sendGet(UtilMethods.urlProcessor(TMDB_URL, parameters)); //make request and get response
            System.out.println(responseJson.toString());
            Map<String, Object> output = response.getOutput();

            if (responseJson.get("results").getAsJsonArray() != null &&
                    (responseJson.get("results").getAsJsonArray().size() != 0) &&
                    responseJson.get("results").getAsJsonArray().get(0).getAsJsonObject().get("overview") != null) {
                String overview = responseJson.get("results").getAsJsonArray().get(0).getAsJsonObject().get("overview").getAsString();
                String[] texts = new String[1];
                texts[0] = overview;

                output.put("text", texts);
                context.put("movieDescription", overview);
                System.out.println("Movie Overview " + overview);
            } else {
                String[] texts = new String[1];
                texts[0] = "Unfortunately I have no information on that movie.";
                output.put("text", texts);

            }
            System.out.println("Output: " + output.toString());
            System.out.println("returning response");
            return response;
        }


        return response;
    }


    // HTTP GET request
    private JsonObject sendGet(String url) throws Exception { //TODO: make asynchronous


        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");


        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        //print result
        JsonParser parser = new JsonParser();
        System.out.println(response.toString());

        return parser.parse(response.toString()).getAsJsonObject();

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

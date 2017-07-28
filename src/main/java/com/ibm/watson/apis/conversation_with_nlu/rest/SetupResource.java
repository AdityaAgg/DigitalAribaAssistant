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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.apis.conversation_with_nlu.listener.AppServletContextListener;
import com.ibm.watson.apis.conversation_with_nlu.utils.Constants;
import com.ibm.watson.apis.conversation_with_nlu.utils.Messages;
import com.ibm.watson.apis.conversation_with_nlu.utils.UtilMethods;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * The SetupResource is used to fetch the configuration file with the WorkspaceId from the env file and the setup
 * configuration stage that the application is currently in. The API endpoint points to '/rest/setup'
 */
@Path("setup")
public class SetupResource {
    private static final Logger logger = LogManager.getLogger(SetupResource.class.getName());

    /**
     * Method to fetch config JSON object and also the workspace_id.
     *
     * @return response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfig() {

        //set oauth token
        setAccessToken();





        //fetch config JSON object and workspace_id
        String workspaceId = System.getenv(Constants.WORKSPACE_ID);
        logger.debug(MessageFormat.format(Messages.getString("SetupResource.WORKSPACE_ID_IS"), workspaceId));

        JsonObject config = new JsonObject();
        config.addProperty(Constants.SETUP_STATUS_MESSAGE, Messages.getString("SetupResource.SETUP_STATUS_MSG"));
        config.addProperty(Constants.SETUP_STEP, "0");
        config.addProperty(Constants.SETUP_STATE, Constants.NOT_READY);
        config.addProperty(Constants.SETUP_PHASE, Messages.getString("SetupResource.PHASE_ERROR"));
        config.addProperty(Constants.SETUP_MESSAGE, Messages.getString("SetupResource.CHECK_LOGS"));

        // Fetch the updated config JSON object from the servlet listener
        config = new AppServletContextListener().getJsonConfig();
        config.addProperty(Constants.SETUP_STATUS_MESSAGE, Messages.getString("SetupResource.SETUP_STATUS_MSG"));
        logger.debug(Messages.getString("SetupResource.CONFIG_STATUS") + config);

        config.addProperty(Constants.WORKSPACE_ID, workspaceId);

        if (config.has(Constants.SETUP_STEP) && (config.get(Constants.SETUP_STEP).getAsInt() == 3)
                && config.get(Constants.SETUP_STATE).getAsString().equalsIgnoreCase(Constants.READY)) {
            if (StringUtils.isBlank(workspaceId)) {
                config.addProperty(Constants.SETUP_STEP, "0");
                config.addProperty(Constants.SETUP_STATE, Constants.NOT_READY);
                config.addProperty(Constants.SETUP_PHASE, Messages.getString("SetupResource.PHASE_ERROR"));
                config.addProperty(Constants.SETUP_MESSAGE, Messages.getString("SetupResource.WORKSPACE_ID_ERROR"));
            }
        }

        return Response.ok(config.getAsJsonObject().toString().trim()).type(MediaType.APPLICATION_JSON)
                .header("Cache-Control", "no-cache").build();
    }

    private void setAccessToken() {
        String json = "scope=eforms%2Cmds&uniq_attr1=PasswordAdapter1&uniq_attr2=p2pTeSg&user_name=cnoll&custom_attributes=%7B%22requestOrigin%22+%3A+%22Buyer%22%2C%22realmId%22%3A%22p2pTeSg%22%2C+%22system%22%3A%22Buyer%22%2C%22anId%22%3A%22AN71000003015%22%7D";



        Headers authHeaders = new Headers.Builder().add("Authorization", "Basic ZWZvcm1zLWNsaWVudDpwcml2YXRlZWZvcm1zMQ==").add("cache-control", "no-cache").add("Content-Type", "application/x-www-form-urlencoded").build();
        //add("postman-token", "9ecd85e1-2d64-41be-9b73-da5347583287")
        Request oauthRequest = UtilMethods.requestBuilder("http://localhost:8092/private/v2/oauth/token", null,  authHeaders, json, Constants.POST, Constants.FORM_DATA);
        OkHttpClient httpClient = new OkHttpClient();
        try {
            okhttp3.Response oauthResponse = httpClient.newCall(oauthRequest).execute();
            JsonParser parser = new JsonParser();
           // System.out.println("authResponse test 2.0 " + oauthResponse.body().string());
            JsonObject authJsonResponse = parser.parse(oauthResponse.body().string()).getAsJsonObject();

            Constants.ACCESS_TOKEN = "Bearer "  + authJsonResponse.get("access_token").getAsString();
            System.out.println(Constants.ACCESS_TOKEN);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

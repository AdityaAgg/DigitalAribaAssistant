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
package com.ibm.watson.apis.conversation_with_nlu.utils;

import okhttp3.MediaType;

/**
 * The Class Constants.
 */
public class Constants {

    private Constants() {

    }

    /**
     * The Constant CONVERSATION_URL.
     */
    public static final String CONVERSATION_URL = "https://gateway.watsonplatform.net/conversation/api";

    /**
     * The Constant DISCOVERY_FIELD_BODY.
     */
    public static final String DISCOVERY_FIELD_BODY = "alchemyapi_text";

    /**
     * The Constant DISCOVERY_FIELD_CONFIDENCE.
     */
    public static final String DISCOVERY_FIELD_CONFIDENCE = "score";



    public static final String DISCOVERY_FIELD_SOURCE ="host";

    /**
     * The Constant DISCOVERY_FIELD_ID.
     */
    public static final String DISCOVERY_FIELD_ID = "id";

    /**
     * The Constant DISCOVERY_FIELD_SOURCE_URL.
     */
    public static final String DISCOVERY_FIELD_SOURCE_URL = "url";

    /**
     * The Constant DISCOVERY_FIELD_TITLE.
     */
    public static final String DISCOVERY_FIELD_TITLE = "title";




    public static final String DISCOVERY_FIELD_DATE = "yyyymmdd";

    /**
     * The Constant DISCOVERY_MAX_SEARCH_RESULTS_TO_SHOW.
     */
    public static final int DISCOVERY_MAX_SEARCH_RESULTS_TO_SHOW = 3;


    public static final String DISCOVERY_URL = "https://gateway.watsonplatform.net/discovery/api";

    public static final String DISCOVERY_VERSION = "2017-06-25";

    public static final String NLU_URL = "https://gateway.watsonplatform.net/natural-language-understanding/api";


    /**
     * The Constant NOT_READY.
     */
    public static final String NOT_READY = "not_ready";

    /**
     * The Constant READY.
     */
    public static final String READY = "ready";

    // Discovery JSON object fields
    /**
     * The Constant SCHEMA_FIELD_SOURCE_URL.
     */
    public static final String SCHEMA_FIELD_SOURCE_URL = "sourceUrl";

    /**
     * The Constant SCHEMA_FIELD_TITLE.
     */
    public static final String SCHEMA_FIELD_TITLE = "title";

    /**
     * The Constant SETUP_MESSAGE.
     */
    public static final String SETUP_MESSAGE = "setup_message";

    /**
     * The Constant SETUP_PHASE.
     */
    public static final String SETUP_PHASE = "setup_phase";

    /**
     * The Constant SETUP_STATE.
     */
    public static final String SETUP_STATE = "setup_state";

    /**
     * The Constant SETUP_STATUS_MESSAGE.
     */
    public static final String SETUP_STATUS_MESSAGE = "setup_status_message";

    /**
     * The Constant SETUP_STEP.
     */
    // Setup config JSON object Fields
    public static final String SETUP_STEP = "setup_step";

    /**
     * The Constant WORKSPACE_ID.
     */
    public static final String WORKSPACE_ID = "WORKSPACE_ID";





    public static final okhttp3.MediaType JSON
            = okhttp3.MediaType.parse("application/json; charset=utf-8");
    public static final MediaType FORM_DATA = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");
    public static final int GET = 0;
    public static final int POST = 1;
    public static final int PUT = 2;
    public static final int DELETE = 3;


    public static String ACCESS_TOKEN = "";

    public static final double BUDGET = 400000;

}

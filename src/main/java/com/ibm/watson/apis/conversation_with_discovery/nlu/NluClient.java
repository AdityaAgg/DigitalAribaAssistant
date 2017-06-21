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
package com.ibm.watson.apis.conversation_with_discovery.nlu;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.*;

/**
 * The Class NluQuery.
 */
public class NluClient {

    private String collectionId;

    private NaturalLanguageUnderstanding naturalLanguageUnderstanding;


    private String password;

    private String userName;


    /**
     * Instantiates a new nlu query.
     */
    public NluClient() {
        userName = System.getenv("NLU_USERNAME");
        password = System.getenv("NLU_PASSWORD");


        naturalLanguageUnderstanding = new NaturalLanguageUnderstanding(
                NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
                userName,
                password
        );
    }





    /**
     * Use the Watson Developer Cloud SDK to send the user's query to the nlu service.
     *
     * @param userQuery The user's query to be sent to the nlu service
     * @return The query responses obtained from the nlu service
     * @throws Exception the exception
     */
    public AnalysisResults query(String userQuery) throws Exception {


        EntitiesOptions entities = new EntitiesOptions.Builder().model("10:c9428809-0405-4fd7-b107-299274e75aec").limit(1).build();
        Features features = new Features.Builder().entities(entities).build();
        AnalyzeOptions parameters = new AnalyzeOptions.Builder().text(userQuery).features(features).clean(false).build();
        AnalysisResults results = naturalLanguageUnderstanding.analyze(parameters).execute();


        return results;
    }
}

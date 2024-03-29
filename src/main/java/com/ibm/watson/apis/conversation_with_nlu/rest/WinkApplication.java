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

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * The Class WinkApplication.
 */
@ApplicationPath("/rest")
/**
 * The Wink application which is used to add endpoints to the server.
 *
 */
public class WinkApplication extends Application {

  /*
   * (non-Javadoc)
   *
   * @see javax.ws.rs.core.Application#getClasses()
   */
  @Override
  public Set<Class<?>> getClasses() {
    // Returns the list of classes which are to be added as REST endpoints
    Set<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(ProxyResource.class);
    classes.add(SetupResource.class);
    return classes;
  }
}

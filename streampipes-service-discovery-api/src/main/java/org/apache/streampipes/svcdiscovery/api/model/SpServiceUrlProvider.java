/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.svcdiscovery.api.model;

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTag;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;

import java.util.StringJoiner;

public enum SpServiceUrlProvider {

  DATA_PROCESSOR(SpServicePathPrefix.DATA_PROCESSOR, SpServiceTagPrefix.DATA_PROCESSOR), DATA_SINK(
          SpServicePathPrefix.DATA_SINK, SpServiceTagPrefix.DATA_SINK), DATA_STREAM(SpServicePathPrefix.DATA_STREAM,
                  SpServiceTagPrefix.DATA_STREAM), ADAPTER(SpServicePathPrefix.ADAPTER, SpServiceTagPrefix.ADAPTER);

  private final String slash = "/";

  private final String prefix;
  private final SpServiceTagPrefix serviceTagPrefix;

  SpServiceUrlProvider(String prefix, SpServiceTagPrefix serviceTagPrefix) {
    this.prefix = prefix;
    this.serviceTagPrefix = serviceTagPrefix;
  }

  public String getPrefix() {
    return this.prefix;
  }

  public String getInvocationUrl(String baseUrl, String appId) {
    return baseUrl + slash + this.prefix + slash + appId;
  }

  public String getIconUrl(String baseUrl, String appId) {
    return new StringJoiner(slash).add(baseUrl).add(this.prefix).add(appId).add("assets").add("icon").toString();
  }

  public SpServiceTag getServiceTag(String appId) {
    return SpServiceTag.create(serviceTagPrefix, appId);
  }
}

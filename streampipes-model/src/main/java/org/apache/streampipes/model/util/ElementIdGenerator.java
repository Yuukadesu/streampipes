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
package org.apache.streampipes.model.util;

import org.apache.commons.lang3.RandomStringUtils;

public class ElementIdGenerator {

  private static final String prefix = "sp:";

  public static String makeElementId(Object obj) {
    return makeElementId(obj.getClass());
  }

  public static String makeElementId(Class<?> clazz) {
    return makeFixedElementId(clazz) + ":" + RandomStringUtils.randomAlphabetic(6);
  }

  public static String makeFixedElementId(Class<?> clazz) {
    return prefix + clazz.getSimpleName().toLowerCase();
  }

  public static String makeElementIdFromAppId(String appId) {
    return prefix + appId;
  }
}

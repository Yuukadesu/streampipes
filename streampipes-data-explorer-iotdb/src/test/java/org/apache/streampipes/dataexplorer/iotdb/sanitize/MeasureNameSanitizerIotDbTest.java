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
package org.apache.streampipes.dataexplorer.iotdb.sanitize;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MeasureNameSanitizerIotDbTest {

  @Test
  public void sanitize() {
    var sanitizer = new MeasureNameSanitizerIotDb();

    assertEquals("myMeasure", sanitizer.sanitize("myMeasure"));
    assertEquals("my_Measure", sanitizer.sanitize("my.Measure"));
    assertEquals("我的措施", sanitizer.sanitize("我的措施"));
    assertEquals("my_Measure", sanitizer.sanitize("my-Measure"));
    assertEquals("my_Measure_", sanitizer.sanitize("my-Measure?"));
    assertEquals("my_", sanitizer.sanitize("my🙂"));
  }
}
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
package org.apache.streampipes.dataexplorer.influx.sanitize;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.DataLakeMeasurementSanitizer;
import org.apache.streampipes.model.datalake.DataLakeMeasure;

public class DataLakeMeasurementSanitizerInflux extends DataLakeMeasurementSanitizer {
  public DataLakeMeasurementSanitizerInflux(IStreamPipesClient client, DataLakeMeasure measure) {
    super(client, measure);
  }

  @Override
  protected void cleanDataLakeMeasure() throws SpRuntimeException {
    // Sanitize the data lake measure name
    measure.setMeasureName(new MeasureNameSanitizer().sanitize(measure.getMeasureName()));

    // Removes all spaces with _ and validates that no special terms are used as runtime names
    measure.getEventSchema().getEventProperties()
            .forEach(ep -> ep.setRuntimeName(InfluxNameSanitizer.renameReservedKeywords(ep.getRuntimeName())));
  }
}

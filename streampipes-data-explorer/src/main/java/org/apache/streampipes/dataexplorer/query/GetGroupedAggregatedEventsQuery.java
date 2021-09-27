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
package org.apache.streampipes.dataexplorer.query;

import org.apache.streampipes.dataexplorer.param.GroupedAggregatedTimeBoundQueryParams;
import org.apache.streampipes.dataexplorer.template.QueryTemplates;
import org.apache.streampipes.model.datalake.SpQueryResult;

public class GetGroupedAggregatedEventsQuery extends ParameterizedDataExplorerQuery<GroupedAggregatedTimeBoundQueryParams, SpQueryResult> {

  public GetGroupedAggregatedEventsQuery(GroupedAggregatedTimeBoundQueryParams queryParams) {
    super(queryParams);
  }

  @Override
  protected void getQuery(DataExplorerQueryBuilder queryBuilder) {
    queryBuilder.add("SELECT mean(*), count(*) FROM " + params.getIndex());
    queryBuilder.add(QueryTemplates.whereTimeWithin(params.getStartDate(), params.getEndDate()));
    queryBuilder.add("GROUP BY " + params.getGroupingTag() + ",time("
            + params.getAggregationValue() + params.getAggregationUnit()
            + ") fill(none) ");
    queryBuilder.add("ORDER BY time ");
  }

  @Override
  protected SpQueryResult postQuery(org.influxdb.dto.QueryResult result) {
    return convertMultiResult(result);
  }
}

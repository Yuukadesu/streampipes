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
package org.apache.streampipes.processors.siddhi.trend;

import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.apache.streampipes.test.generator.EventStreamGenerator;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;
import org.apache.streampipes.test.generator.grounding.EventGroundingGenerator;
import org.apache.streampipes.wrapper.params.generator.DataProcessorParameterGenerator;
import org.apache.streampipes.wrapper.siddhi.engine.callback.SiddhiDebugCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestTrendProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(TrendProcessor.class);

  static Stream<Arguments> data() {
    return Stream.of(
            Arguments.of(1, 100, TrendOperator.INCREASE, Arrays.asList(new Tuple2<>(100, 1), new Tuple2<>(100, 2)), 1),
            Arguments.of(1, 100, TrendOperator.INCREASE,
                    Arrays.asList(new Tuple2<>(100, 1), new Tuple2<>(100, 1), new Tuple2<>(100, 2)), 1),
            Arguments.of(1, 100, TrendOperator.DECREASE,
                    Arrays.asList(new Tuple2<>(100, 1), new Tuple2<>(100, 1), new Tuple2<>(100, 0)), 1),
            Arguments.of(1, 100, TrendOperator.INCREASE,
                    Arrays.asList(new Tuple2<>(100, 1), new Tuple2<>(100, 1), new Tuple2<>(100, 2),
                            new Tuple2<>(100, 1), new Tuple2<>(100, 2)),
                    2),
            Arguments.of(1, 200, TrendOperator.INCREASE, Arrays.asList(new Tuple2<>(100, 1), new Tuple2<>(100, 1),
                    new Tuple2<>(100, 2), new Tuple2<>(100, 1), new Tuple2<>(100, 2)), 0));
  }

  @ParameterizedTest
  @MethodSource("data")
  public void testTrend(int timeWindow, int increase, TrendOperator trendOperator,
          List<Tuple2<Integer, Integer>> eventSettings, int expectedMatchCount) {
    final Integer[] actualMatchCount = {0};
    DataProcessorDescription originalGraph = new TrendProcessor().declareModel();
    originalGraph.setSupportedGrounding(EventGroundingGenerator.makeDummyGrounding());

    DataProcessorInvocation graph = InvocationGraphGenerator.makeEmptyInvocation(originalGraph);

    graph.setInputStreams(Collections
            .singletonList(EventStreamGenerator.makeStreamWithProperties(Collections.singletonList("randomValue"))));

    graph.setOutputStrategies(graph.getOutputStrategies().stream().filter(o -> o instanceof CustomOutputStrategy)
            .peek(o -> ((CustomOutputStrategy) o).setSelectedPropertyKeys(List.of("s0::randomValue")))
            .collect(Collectors.toList()));

    graph.setOutputStream(EventStreamGenerator.makeStreamWithProperties(Collections.singletonList("randomValue")));

    graph.getOutputStream().getEventGrounding().getTransportProtocol().getTopicDefinition()
            .setActualTopicName("output-topic");

    var visitor = new TrendConfigurationVisitor("s0::randomValue", trendOperator, increase, timeWindow);
    graph.getStaticProperties().forEach(sp -> sp.accept(visitor));

    var processorParams = new DataProcessorParameterGenerator().makeParameters(graph);

    SiddhiDebugCallback callback = new SiddhiDebugCallback() {
      @Override
      public void onEvent(io.siddhi.core.event.Event event) {
        actualMatchCount[0]++;
      }

      @Override
      public void onEvent(List<io.siddhi.core.event.Event> events) {

      }
    };

    TrendProcessor trend = new TrendProcessor(callback);
    trend.onPipelineStarted(processorParams, null, null);

    sendEvents(trend, eventSettings);
    LOG.info("Expected match count is {}", expectedMatchCount);
    LOG.info("Actual match count is {}", actualMatchCount[0]);
    Assertions.assertEquals(expectedMatchCount, actualMatchCount[0]);
  }

  private void sendEvents(TrendProcessor trend, List<Tuple2<Integer, Integer>> eventSettings) {
    List<Tuple2<Integer, Event>> events = makeEvents(eventSettings);
    for (Tuple2<Integer, Event> event : events) {
      LOG.info("Sending event with value " + event.v.getFieldBySelector("s0::randomValue"));
      trend.onEvent(event.v, null);
      try {
        Thread.sleep(event.k);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private List<Tuple2<Integer, Event>> makeEvents(List<Tuple2<Integer, Integer>> eventSettings) {
    List<Tuple2<Integer, Event>> events = new ArrayList<>();
    for (Tuple2<Integer, Integer> eventSetting : eventSettings) {
      events.add(makeEvent(eventSetting.k, eventSetting.v));
    }
    return events;
  }

  private Tuple2<Integer, Event> makeEvent(Integer timeout, Integer value) {
    Map<String, Object> map = new HashMap<>();
    map.put("randomValue", value);
    return new Tuple2<>(timeout, EventFactory.fromMap(map, new SourceInfo("test" + "-topic", "s0"),
            new SchemaInfo(null, new ArrayList<>())));
  }
}

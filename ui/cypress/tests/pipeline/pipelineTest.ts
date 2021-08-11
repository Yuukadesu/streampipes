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

import { AdapterUtils } from '../../support/utils/AdapterUtils';
import { PipelineUtils } from '../../support/utils/PipelineUtils';
import { PipelineElementBuilder } from '../../support/builder/PipelineElementBuilder';
import { PipelineBuilder } from '../../support/builder/PipelineBuilder';

const adapterName = 'simulator';

before('Setup Test', () => {
  it('Initialize Test', () => {
    cy.initStreamPipesTest();
  });

  AdapterUtils.addMachineDataSimulator(adapterName);
});

describe('Test Random Data Simulator Stream Adapter', () => {

 const pipelineInput = PipelineBuilder.create('Pipeline Test')
    .addSource(adapterName)
    .addProcessingElement(
      PipelineElementBuilder.create('field_renamer')
        .addInput('drop-down', 'convert-property', 'timestamp')
        .addInput('input', 'field-name', 't')
        .build())
    .addSink(
      PipelineElementBuilder.create('dashboard_sink')
        .addInput('input', 'visualization-name', 'Demo')
        .build())
    .build();

  PipelineUtils.testPipeline(pipelineInput);
});


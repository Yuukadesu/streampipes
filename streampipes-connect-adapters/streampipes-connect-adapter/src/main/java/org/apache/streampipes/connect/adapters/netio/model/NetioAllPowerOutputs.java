/*
Copyright 2020 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.apache.streampipes.connect.adapters.netio.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
public class NetioAllPowerOutputs {

    @SerializedName("GlobalMeasure")
    private NetioGlobalMeasure gobalMeasure;

    @SerializedName("Outputs")
    private NetioPowerOutput[] powerOutputs;


    public NetioGlobalMeasure getGobalMeasure() {
        return gobalMeasure;
    }

    public void setGobalMeasure(NetioGlobalMeasure gobalMeasure) {
        this.gobalMeasure = gobalMeasure;
    }

    public NetioPowerOutput[] getPowerOutputs() {
        return powerOutputs;
    }

    public void setPowerOutputs(NetioPowerOutput[] powerOutputs) {
        this.powerOutputs = powerOutputs;
    }
}

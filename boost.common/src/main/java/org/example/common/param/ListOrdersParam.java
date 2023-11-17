/*
*Copyright (c) Alibaba Group;
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at

*   http://www.apache.org/licenses/LICENSE-2.0

*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/

package org.example.common.param;

import lombok.Data;
import org.example.common.constant.TradeStatus;
import org.example.common.helper.BaseOtsHelper.OtsFilter;

import java.util.List;

@Data
public class ListOrdersParam {

    private List<OtsFilter> matchFilters;

    private List<OtsFilter> queryFilters;

    private String serviceInstanceId;

    private String startTime;

    private String endTime;

    private TradeStatus tradeStatus;

    private Integer maxResults;

    private String nextToken;
}

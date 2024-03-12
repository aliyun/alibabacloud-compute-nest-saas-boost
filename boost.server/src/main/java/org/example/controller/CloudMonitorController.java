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

package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.model.MetricDatasModel;
import org.example.common.model.MetricMetaDataModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.ListMetricsParam;
import org.example.service.base.CloudMonitorService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
@Api(value="cloudMonitor",tags={"cloudMonitor"})
public class CloudMonitorController {
    @Resource
    private CloudMonitorService cloudMonitorService;

    @ApiOperation(value = "获取全部可监控的表单信息", nickname = "listMetricMetaDatas")
    @RequestMapping(path = "/listMetricMetaDatas",method = RequestMethod.GET)
    public ListResult<MetricMetaDataModel> listMetricMetaDatas(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel) {
        return cloudMonitorService.listMetricMetaDatas();
    }

    @ApiOperation(value = "获取指定表单的监控数据", nickname = "listMetrics")
    @RequestMapping(path = "/listMetrics",method = RequestMethod.GET)
    public BaseResult<MetricDatasModel> listMetrics(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel, ListMetricsParam listMetricsParam) {
        return cloudMonitorService.listMetrics(userInfoModel, listMetricsParam);
    }
}

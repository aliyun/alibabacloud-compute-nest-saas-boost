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

package org.example.service.parameter.impl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import static org.example.common.constant.Constants.SERVICE_INSTANCE_ID;
import org.example.common.helper.oos.ParameterOosHelper;
import org.example.common.model.ConfigParameterModel;
import org.example.common.model.ConfigParameterQueryModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.parameter.ListConfigParametersParam;
import org.example.common.param.parameter.UpdateConfigParameterParam;
import org.example.service.parameter.ParameterManagerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ParameterManagerServiceImpl implements ParameterManagerService {

    @Value("${stack-name}")
    private String stackName;
    @Resource
    private ParameterOosHelper parameterOosHelper;

    @Override
    public ListResult<ConfigParameterModel> listConfigParameters(UserInfoModel userInfoModel, ListConfigParametersParam listConfigParametersParam) {
        ListResult<ConfigParameterModel> results = new ListResult<>();
        results.setData(new ArrayList<>());
        List<ConfigParameterQueryModel> queries = listConfigParametersParam.getConfigParameterQueryModels();
        if (queries == null || queries.isEmpty()) {
            results.setMessage("Invalid query: 'encrypted' must not be null and 'name' must not be null or empty");
            return results;
        }

        List<String> secretNameList = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        for(ConfigParameterQueryModel configParameterQueryModel : listConfigParametersParam.getConfigParameterQueryModels()){
            String format = String.format("%s-%s-%s", SERVICE_INSTANCE_ID, stackName, configParameterQueryModel.getName());
            if (configParameterQueryModel.getEncrypted()) {
                secretNameList.add(format);
            } else {
                nameList.add(format);
            }
        }

        List<ConfigParameterModel> secretParametersList = parameterOosHelper.listSecretParameters(secretNameList);
        List<ConfigParameterModel> parametersList = parameterOosHelper.listParameters(nameList);
        List<ConfigParameterModel> listConfigParameters = new ArrayList<>(secretParametersList);
        listConfigParameters.addAll(parametersList);
        for (ConfigParameterModel configParameterModel : listConfigParameters) {
            configParameterModel.setName(configParameterModel.getName().replace(String.format("%s-%s-", SERVICE_INSTANCE_ID, stackName), ""));
        }
        results.setData(listConfigParameters);
        return results;
    }

    @Override
    public BaseResult<Void> updateConfigParameter(UserInfoModel userInfoModel, UpdateConfigParameterParam updateConfigParameterParam) {
        String format = String.format("%s-%s-%s", SERVICE_INSTANCE_ID, stackName, updateConfigParameterParam.getName());
        updateConfigParameterParam.setName(format);
        return parameterOosHelper.updateConfigParameter(updateConfigParameterParam);
    }
}

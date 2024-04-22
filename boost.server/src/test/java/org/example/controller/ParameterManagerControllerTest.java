package org.example.controller;

import java.util.ArrayList;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.model.ConfigParameterModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.parameter.ListConfigParametersParam;
import org.example.common.param.parameter.UpdateConfigParameterParam;
import org.example.service.parameter.ParameterManagerService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParameterManagerControllerTest {

    @Tested
    private ParameterManagerController controller;

    @Injectable
    private ParameterManagerService parameterManagerService;

    @Before
    public void setUp() {
        controller = new ParameterManagerController();
    }

    @Test
    public void testListConfigParameters() {
        UserInfoModel userInfoModel = new UserInfoModel();
        ListConfigParametersParam listConfigParametersParam = new ListConfigParametersParam();
        ConfigParameterModel expectedModel = new ConfigParameterModel();
        ListResult<ConfigParameterModel> expectedResponse = new ListResult<ConfigParameterModel>();
        expectedResponse.setData(new ArrayList<>());
        expectedResponse.getData().add(expectedModel);
        new Expectations() {{
            parameterManagerService.listConfigParameters(userInfoModel, listConfigParametersParam);
            result = expectedResponse;
        }};

        ListResult<ConfigParameterModel> response = controller.listConfigParameters(userInfoModel, listConfigParametersParam);
        assertEquals(expectedResponse.getData(), response.getData());
    }

    @Test
    public void testUpdateConfigParameter() {
        UserInfoModel userInfoModel = new UserInfoModel();
        UpdateConfigParameterParam updateConfigParameterParam = new UpdateConfigParameterParam();
        BaseResult<Void> expectedResult = new BaseResult<>();

        new Expectations() {{
            parameterManagerService.updateConfigParameter(userInfoModel, updateConfigParameterParam);
            result = expectedResult;
        }};

        BaseResult<Void> response = controller.updateConfigParameter(userInfoModel, updateConfigParameterParam);
        assertEquals(expectedResult, response);
    }
}

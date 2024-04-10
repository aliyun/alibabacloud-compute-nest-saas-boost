package org.example.controller;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.example.common.BaseResult;
import org.example.common.model.ListConfigParametersModel;
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
        ListConfigParametersModel expectedResponse = new ListConfigParametersModel();

        new Expectations() {{
            parameterManagerService.listConfigParameters(userInfoModel, listConfigParametersParam);
            result = new BaseResult<>(expectedResponse);
        }};

        BaseResult<ListConfigParametersModel> response = controller.listConfigParameters(userInfoModel, listConfigParametersParam);
        assertEquals(expectedResponse, response.getData());
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

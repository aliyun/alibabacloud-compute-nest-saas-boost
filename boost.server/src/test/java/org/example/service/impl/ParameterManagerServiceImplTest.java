package org.example.service.impl;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.example.common.BaseResult;
import org.example.common.helper.oos.ParameterOosHelper;
import org.example.common.model.ListConfigParametersModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.parameter.ListConfigParametersParam;
import org.example.common.param.parameter.UpdateConfigParameterParam;
import org.example.service.parameter.impl.ParameterManagerServiceImpl;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class ParameterManagerServiceImplTest {

    @Tested
    private ParameterManagerServiceImpl service;

    @Injectable
    private ParameterOosHelper parameterOosHelper;

    @Before
    public void setUp() {
        service = new ParameterManagerServiceImpl();
    }

    @Test
    public void testListConfigParameters() {
        UserInfoModel userInfoModel = new UserInfoModel();
        ListConfigParametersParam listConfigParametersParam = new ListConfigParametersParam();
        ListConfigParametersModel expectedResponse = new ListConfigParametersModel();

        new Expectations() {{
            parameterOosHelper.listConfigParameters(listConfigParametersParam);
            result = new BaseResult<>(expectedResponse);
        }};

        BaseResult<ListConfigParametersModel> response = service.listConfigParameters(userInfoModel, listConfigParametersParam);

        assertEquals(expectedResponse, response.getData());
    }

    @Test
    public void testUpdateConfigParameter() {
        UserInfoModel userInfoModel = new UserInfoModel();
        UpdateConfigParameterParam updateConfigParameterParam = new UpdateConfigParameterParam();
        BaseResult<Void> expectedResult = new BaseResult<>();

        new Expectations() {{
            parameterOosHelper.updateConfigParameter(updateConfigParameterParam);
            result = expectedResult;
        }};

        BaseResult<Void> response = service.updateConfigParameter(userInfoModel, updateConfigParameterParam);
        assertEquals(expectedResult, response);
    }
}

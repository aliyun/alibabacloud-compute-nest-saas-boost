package org.example.service.impl;

import java.util.Arrays;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.helper.oos.ParameterOosHelper;
import org.example.common.model.ConfigParameterModel;
import org.example.common.model.ConfigParameterQueryModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.parameter.ListConfigParametersParam;
import org.example.common.param.parameter.UpdateConfigParameterParam;
import org.example.service.parameter.impl.ParameterManagerServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParameterManagerServiceImplTest {

    @Tested
    private ParameterManagerServiceImpl parameterManagerService;

    @Injectable
    private ParameterOosHelper parameterOosHelper;

    private final UserInfoModel userInfoModel = new UserInfoModel();

    private final UpdateConfigParameterParam updateConfigParameterParam = new UpdateConfigParameterParam();

    @BeforeEach
    void setUp() {
        // Optionally set up default values for test inputs here.
    }

    @Test
    void testUpdateConfigParameter() {
        BaseResult<Void> expectedResult = new BaseResult<>();
        // Set up expected data in the result object.

        new Expectations() {{
            parameterOosHelper.updateConfigParameter(updateConfigParameterParam);
            result = expectedResult;
        }};

        BaseResult<Void> actualResult = parameterManagerService.updateConfigParameter(userInfoModel, updateConfigParameterParam);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testListConfigParameters() {
        ListResult<ConfigParameterModel> expectedListResult = new ListResult<>();
        ListConfigParametersParam listConfigParametersParam = new ListConfigParametersParam();
        ConfigParameterQueryModel configParameterQueryModel = new ConfigParameterQueryModel();
        configParameterQueryModel.setName("name");
        configParameterQueryModel.setEncrypted(true);
        listConfigParametersParam.setConfigParameterQueryModels(Arrays.asList(configParameterQueryModel));
        ConfigParameterModel expectedConfigParameterModel = new ConfigParameterModel();
        expectedConfigParameterModel.setName("adjusted-name");
        expectedListResult.setData(Arrays.asList(expectedConfigParameterModel));

        new Expectations() {{
            parameterOosHelper.listConfigParameters(withAny(listConfigParametersParam));
            result = expectedListResult;
        }};

        ListResult<ConfigParameterModel> actualListResult = parameterManagerService.listConfigParameters(userInfoModel, listConfigParametersParam);

        assertEquals(expectedListResult, actualListResult);
    }

    @Test
    void testUpdateConfigParameterWithNullResponse() {
        new Expectations() {{
            parameterOosHelper.updateConfigParameter(updateConfigParameterParam);
            result = null;
        }};

        BaseResult<Void> actualResult = parameterManagerService.updateConfigParameter(userInfoModel, updateConfigParameterParam);

        assertNull(actualResult);
    }
}